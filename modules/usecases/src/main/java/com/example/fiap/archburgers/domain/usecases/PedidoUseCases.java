package com.example.fiap.archburgers.domain.usecases;

import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.datagateway.PedidoGateway;
import com.example.fiap.archburgers.domain.entities.ItemCardapio;
import com.example.fiap.archburgers.domain.entities.Pedido;
import com.example.fiap.archburgers.domain.exception.DomainPermissionException;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosLocal;
import com.example.fiap.archburgers.domain.external.PagamentoService;
import com.example.fiap.archburgers.domain.external.PainelPedidos;
import com.example.fiap.archburgers.domain.usecaseparam.CriarPedidoParam;
import com.example.fiap.archburgers.domain.utils.Clock;
import com.example.fiap.archburgers.domain.utils.StringUtils;
import com.example.fiap.archburgers.domain.valueobjects.Cpf;
import com.example.fiap.archburgers.domain.valueobjects.PedidoDetalhe;
import com.example.fiap.archburgers.domain.valueobjects.StatusPedido;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public PedidoDetalhe criarPedido(CriarPedidoParam param, UsuarioLogado usuarioLogado) throws DomainPermissionException {
        if (param == null)
            throw new IllegalArgumentException("Parameter missing");
        if (param.idCarrinho() == null)
            throw new IllegalArgumentException("idCarrinho missing");
        if (StringUtils.isEmpty(param.formaPagamento()))
            throw new IllegalArgumentException("formaPagamento missing");

        var formaPagamento = pagamentoService.validarFormaPagamento(param.formaPagamento());

        var carrinho = carrinhoGateway.getCarrinho(param.idCarrinho());
        if (carrinho == null) {
            throw new IllegalArgumentException("Invalid idCarrinho " + param.idCarrinho());
        }

        if (carrinho.idClienteIdentificado() != null) {
            if (!usuarioLogado.autenticado())
                throw new DomainPermissionException("Usuário não autenticado, carrinho pertence a usuário cadastrado. " + usuarioLogado.authError());

            var clienteLogado = clienteGateway.getClienteByCpf(new Cpf(usuarioLogado.getCpf()));
            if (clienteLogado == null)
                throw new RuntimeException("Registro inconsistente! Usuario logado [" + usuarioLogado.getCpf() + "] não cadastrado na base");

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

        pagamentoService.iniciarPagamento(saved);

        carrinhoGateway.deleteCarrinho(carrinho);

        return new PedidoDetalhe(saved, detalheItens);
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

//    public Pedido finalizarPagamento(int idPedido, String newIdPedidoSistemaExterno) {
//        Pedido pedido = pedidoGateway.getPedido(idPedido);
//
//        if (pedido == null) {
//            throw new DomainArgumentException("Pedido invalido=" + idPedido);
//        }
//
//        Pagamento inicial = pagamentoGateway.findPagamentoByPedido(idPedido);
//
//        if (inicial == null) {
//            throw new DomainArgumentException("Pagamento nao encontrado. Pedido=" + idPedido);
//        }
//
//        pedido = pedido.withItens(itemCardapioGateway.findByPedido(idPedido));
//
//        if (inicial.status() != StatusPagamento.PENDENTE) {
//            throw new DomainArgumentException("Pagamento nao está Pendente. Pedido=" + idPedido);
//        }
//
//        LocalDateTime dataHoraPagamento = clock.localDateTime();
//
//        Pagamento pagamentoFinalizado;
//        if (newIdPedidoSistemaExterno != null && !newIdPedidoSistemaExterno.equals(inicial.idPedidoSistemaExterno())) {
//            pagamentoFinalizado = inicial.finalizar(dataHoraPagamento, newIdPedidoSistemaExterno);
//        } else {
//            pagamentoFinalizado = inicial.finalizar(dataHoraPagamento);
//        }
//
//        var pedidoPago = pedido.confirmarPagamento(pagamentoFinalizado);
//
//        pagamentoGateway.updateStatus(pagamentoFinalizado);
//
//        pedidoGateway.updateStatus(pedidoPago);
//
//        return pedidoPago;
//
//
//        //        var valorTotalItens = ItemCardapio.somarValores(itens.stream().map(ItemPedido::itemCardapio).toList());
////        if (!pagamento.valor().equals(valorTotalItens)) {
////            throw new DomainArgumentException("Valor do pagamento não corresponde aos itens do pedido. Pedido="
////                    + valorTotalItens + ", Pago=" + pagamento.valor());
////        }
//    }

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
