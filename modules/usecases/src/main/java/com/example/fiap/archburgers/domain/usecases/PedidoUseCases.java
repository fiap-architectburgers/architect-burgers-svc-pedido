package com.example.fiap.archburgers.domain.usecases;

import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.datagateway.PedidoGateway;
import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.external.*;
import com.example.fiap.archburgers.domain.entities.Pedido;
import com.example.fiap.archburgers.domain.exception.DomainPermissionException;
import com.example.fiap.archburgers.domain.usecaseparam.CriarPedidoParam;
import com.example.fiap.archburgers.domain.utils.Clock;
import com.example.fiap.archburgers.domain.utils.StringUtils;
import com.example.fiap.archburgers.domain.valueobjects.Cpf;
import com.example.fiap.archburgers.domain.valueobjects.PedidoDetalhe;
import com.example.fiap.archburgers.domain.valueobjects.StatusPedido;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class PedidoUseCases {
    private final PedidoGateway pedidoGateway;
    private final CarrinhoGateway carrinhoGateway;
    private final ClienteGateway clienteGateway;

    private final CatalogoProdutosLocal catalogo;
    private final PagamentoService pagamentoService;

    private final Clock clock;
    private final PainelPedidos painelPedidos;

    public PedidoUseCases(PedidoGateway pedidoGateway, CarrinhoGateway carrinhoGateway, ClienteGateway clienteGateway,
                          CatalogoProdutosLocal catalogo,
                          PagamentoService pagamentoService,
                          Clock clock, PainelPedidos painelPedidos) {
        this.pedidoGateway = pedidoGateway;
        this.carrinhoGateway = carrinhoGateway;
        this.clienteGateway = clienteGateway;
        this.catalogo = catalogo;
        this.pagamentoService = pagamentoService;

        this.clock = clock;
        this.painelPedidos = painelPedidos;
    }

    public PedidoDetalhe getPedido(Integer idPedido) {
        var pedido = pedidoGateway.getPedido(idPedido);
        if (pedido == null) {
            return null;
        }

        var itens = catalogo.findAll(pedido.itens());
        return new PedidoDetalhe(pedido, itens);
    }

    public PedidoDetalhe criarPedido(CriarPedidoParam param, UsuarioLogado usuarioLogado) throws DomainPermissionException, Exception {
        if (param == null)
            throw new IllegalArgumentException("Parameter missing");
        if (param.idCarrinho() == null)
            throw new IllegalArgumentException("idCarrinho missing");
        if (StringUtils.isEmpty(param.formaPagamento()))
            throw new IllegalArgumentException("formaPagamento missing");

        var formaPagamento = pagamentoService.validarFormaPagamento(param.formaPagamento());

        var carrinho = carrinhoGateway.getCarrinho(param.idCarrinho());
        if (carrinho == null) {
            throw new DomainArgumentException("Invalid idCarrinho " + param.idCarrinho());
        }

        if (carrinho.idClienteIdentificado() != null) {
            if (!usuarioLogado.autenticado())
                throw new DomainPermissionException("Usuário não autenticado, carrinho pertence a usuário cadastrado. " + usuarioLogado.authError());

            var clienteLogado = clienteGateway.getClienteByCpf(new Cpf(usuarioLogado.getCpf()));
            if (clienteLogado == null)
                throw new IllegalStateException("Registro inconsistente! Usuario logado [" + usuarioLogado.getCpf() + "] não cadastrado na base");

            if (!clienteLogado.id().equals(carrinho.idClienteIdentificado())) {
                throw new DomainPermissionException("Carrinho " + carrinho.id() + " não pertence ao cliente "
                        + clienteLogado.id().id() + "/" + clienteLogado.cpf().cpfNum());
            }
        }

        Map<Integer, ItemCardapio> detalheItens = catalogo.findAll(carrinho.itens());

        var pedido = Pedido.novoPedido(carrinho.idClienteIdentificado(), carrinho.nomeClienteNaoIdentificado(),
                carrinho.itens(), carrinho.observacoes(),
                formaPagamento, clock.localDateTime());

        Pedido saved = pedidoGateway.savePedido(pedido);

        PedidoDetalhe pedidoDetalhe = new PedidoDetalhe(saved, detalheItens);

        pagamentoService.iniciarPagamento(pedidoDetalhe);

        carrinhoGateway.deleteCarrinho(carrinho);

        return pedidoDetalhe;
    }

    public List<PedidoDetalhe> listarPedidosByStatus(@Nullable StatusPedido filtroStatus) {
        if (filtroStatus == null) {
            throw new IllegalArgumentException("Obrigatório informar um filtro");
        }

        var pedidos = pedidoGateway.listPedidos(List.of(filtroStatus), null);

        return pedidos.stream().map(p -> {
            var itens = catalogo.findAll(p.itens());
            return new PedidoDetalhe(p, itens);
        }).toList();
    }

    public List<PedidoDetalhe> listarPedidosComAtraso() {
        var now = clock.localDateTime();
        var olderThan = now.minusMinutes(20);

        var pedidos = pedidoGateway.listPedidos(List.of(StatusPedido.RECEBIDO, StatusPedido.PREPARACAO), olderThan);
        return pedidos.stream().map(p -> {
            var itens = catalogo.findAll(p.itens());
            return new PedidoDetalhe(p, itens);
        }).toList();
    }

    public List<PedidoDetalhe> listarPedidosAtivos() {
        var pedidos = pedidoGateway.listPedidos(List.of(
                StatusPedido.PAGAMENTO, StatusPedido.RECEBIDO, StatusPedido.PREPARACAO, StatusPedido.PRONTO
        ), null);
        return pedidos.stream().map(p -> {
            var itens = catalogo.findAll(p.itens());
            return new PedidoDetalhe(p, itens);
        }).toList();
    }

    public PedidoDetalhe validarPedido(Integer idPedido) {
        return loadAndApply(idPedido, Pedido::validar);
    }

    public PedidoDetalhe cancelarPedido(Integer idPedido) {
        return loadAndApply(idPedido, Pedido::cancelar);
    }

    public PedidoDetalhe setPronto(Integer idPedido) {
        PedidoDetalhe updated = loadAndApply(idPedido, Pedido::setPronto);
        painelPedidos.notificarPedidoPronto(updated.pedido());
        return updated;
    }


    public PedidoDetalhe finalizarPedido(Integer idPedido) {
        return loadAndApply(idPedido, Pedido::finalizar);
    }

    public Pedido finalizarPagamento(Pagamento pagamento) {
        int idPedido = Objects.requireNonNull(pagamento.idPedido(), "Pagamento deve possuir um ID de pedido");

        Pedido pedido = pedidoGateway.getPedido(idPedido);

        if (pedido == null) {
            throw new DomainArgumentException("Pedido invalido=" + idPedido);
        }

        var pedidoPago = pedido.confirmarPagamento(pagamento);

        pedidoGateway.updateStatus(pedidoPago);

        return pedidoPago;
    }

    private @NotNull PedidoDetalhe loadAndApply(Integer idPedido, Function<Pedido, Pedido> update) {
        var pedido = pedidoGateway.getPedido(Objects.requireNonNull(idPedido, "ID não pode ser null"));

        if (pedido == null) {
            throw new IllegalArgumentException("Pedido não encontrado: " + idPedido);
        }

        var atualizado = update.apply(pedido);
        pedidoGateway.updateStatus(atualizado);

        var itens = catalogo.findAll(atualizado.itens());

        return new PedidoDetalhe(atualizado, itens);
    }
}
