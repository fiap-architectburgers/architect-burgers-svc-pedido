package com.example.fiap.archburgers.controller;

import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.datagateway.PedidoGateway;
import com.example.fiap.archburgers.domain.exception.DomainPermissionException;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosService;
import com.example.fiap.archburgers.domain.external.PagamentoService;
import com.example.fiap.archburgers.domain.external.PainelPedidos;
import com.example.fiap.archburgers.domain.usecaseparam.CriarPedidoParam;
import com.example.fiap.archburgers.domain.usecases.PedidoUseCases;
import com.example.fiap.archburgers.domain.utils.Clock;
import com.example.fiap.archburgers.domain.valueobjects.PedidoDetalhe;
import com.example.fiap.archburgers.domain.valueobjects.StatusPedido;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PedidoController {
    private final PedidoUseCases pedidoUseCases;

    public PedidoController(PedidoGateway pedidoGateway, CarrinhoGateway carrinhoGateway,
                            ClienteGateway clienteGateway,
                            CatalogoProdutosService catalogoProdutosService,
                            PagamentoService pagamentoService,
                            Clock clock, PainelPedidos painelPedidos) {

        pedidoUseCases = new PedidoUseCases(pedidoGateway, carrinhoGateway, clienteGateway,
                catalogoProdutosService, pagamentoService, clock, painelPedidos);
    }

    public PedidoDetalhe criarPedido(CriarPedidoParam param, UsuarioLogado usuarioLogado) throws DomainPermissionException {
        return pedidoUseCases.criarPedido(param, usuarioLogado);
    }

    public List<PedidoDetalhe> listarPedidosByStatus(@Nullable StatusPedido filtroStatus) {
        return pedidoUseCases.listarPedidosByStatus(filtroStatus);
    }

    public List<PedidoDetalhe> listarPedidosComAtraso() {
        return pedidoUseCases.listarPedidosComAtraso();
    }

    /**
     * Lista com todos os status exceto Finalizado e Cancelado
     */
    public List<PedidoDetalhe> listarPedidosAtivos() {
        return pedidoUseCases.listarPedidosAtivos();
    }

    public PedidoDetalhe validarPedido(Integer idPedido) {
        return pedidoUseCases.validarPedido(idPedido);
    }

    public PedidoDetalhe cancelarPedido(Integer idPedido) {
        return pedidoUseCases.cancelarPedido(idPedido);
    }

    public PedidoDetalhe setPronto(Integer idPedido) {
        return pedidoUseCases.setPronto(idPedido);
    }

    public PedidoDetalhe finalizarPedido(Integer idPedido) {
        return pedidoUseCases.finalizarPedido(idPedido);
    }
}
