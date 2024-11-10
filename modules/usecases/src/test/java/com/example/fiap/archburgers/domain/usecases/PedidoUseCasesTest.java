package com.example.fiap.archburgers.domain.usecases;

import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.datagateway.PedidoGateway;
import com.example.fiap.archburgers.domain.entities.*;
import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.exception.DomainPermissionException;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosLocal;
import com.example.fiap.archburgers.domain.external.ItemCardapio;
import com.example.fiap.archburgers.domain.external.PagamentoService;
import com.example.fiap.archburgers.domain.external.PainelPedidos;
import com.example.fiap.archburgers.domain.usecaseparam.CriarPedidoParam;
import com.example.fiap.archburgers.domain.utils.Clock;
import com.example.fiap.archburgers.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoUseCasesTest {
    @Mock
    private PedidoGateway pedidoGateway;
    @Mock
    private CarrinhoGateway carrinhoGateway;
    @Mock
    private CatalogoProdutosLocal catalogoProdutosLocal;
    @Mock
    private ClienteGateway clienteGateway;
    @Mock
    private PagamentoService pagamentoService;
    @Mock
    private Clock clock;
    @Mock
    private PainelPedidos painelPedidos;

    private PedidoUseCases pedidoUseCases;

    @BeforeEach
    void setUp() {
        pedidoUseCases = new PedidoUseCases(
                pedidoGateway, carrinhoGateway, clienteGateway,
                catalogoProdutosLocal, pagamentoService,
                clock, painelPedidos);
    }

    @Test
    void criarPedido_missingParam() {
        var usuarioLogado = mock(UsuarioLogado.class);

        assertThrows(IllegalArgumentException.class, () -> pedidoUseCases.criarPedido(null, usuarioLogado));
        assertThrows(IllegalArgumentException.class, () -> pedidoUseCases.criarPedido(
                new CriarPedidoParam(null, "DINHEIRO"), usuarioLogado));
        assertThrows(IllegalArgumentException.class, () -> pedidoUseCases.criarPedido(
                new CriarPedidoParam(12, ""), usuarioLogado));
    }

    @Test
    void criarPedido_invalidPagamento() {
        var usuarioLogado = mock(UsuarioLogado.class);

        when(pagamentoService.validarFormaPagamento("Cheque")).thenThrow(
                new DomainArgumentException("Forma de pagamento inválida: Cheque"));

        assertThat(assertThrows(DomainArgumentException.class, () -> pedidoUseCases.criarPedido(
                new CriarPedidoParam(12, "Cheque"), usuarioLogado))
        ).hasMessage("Forma de pagamento inválida: Cheque");
    }

    @Test
    void criarPedido_ok_clienteIdentificado() {
        var usuarioLogado = mockUsuarioLogado("12332112340");

        List<ItemPedido> itensPedido = List.of(
                new ItemPedido(1, 1000),
                new ItemPedido(2, 1001)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido = Map.of(
                1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger",
                        new ValorMonetario("25.90")),
                1001, new ItemCardapio(1001, TipoItemCardapio.BEBIDA, "Refrigerante", "Refrigerante",
                        new ValorMonetario("5.00"))
        );

        when(carrinhoGateway.getCarrinho(12)).thenReturn(
                Carrinho.carrinhoSalvoClienteIdentificado(12, new IdCliente(25),
                        itensPedido,
                        "Lanche sem cebola",
                        LocalDateTime.of(2024, 5, 18, 14, 0))
        );

        when(clienteGateway.getClienteByCpf(new Cpf("12332112340"))).thenReturn(new Cliente(new IdCliente(25),
                "Mesmo Cliente", new Cpf("12332112340"), "mesmo.cliente@example.com"));

        when(clock.localDateTime()).thenReturn(dateTime);
        when(pagamentoService.validarFormaPagamento("DINHEIRO")).thenReturn(FORMA_PAGAMENTO_DINHEIRO);

        when(catalogoProdutosLocal.findAll(itensPedido)).thenReturn(detalhesItensPedido);

        var expectedPedido = Pedido.novoPedido(new IdCliente(25), null, itensPedido,
                "Lanche sem cebola", FORMA_PAGAMENTO_DINHEIRO, dateTime);

        when(pedidoGateway.savePedido(expectedPedido)).thenReturn(expectedPedido.withId(33));

        var result = pedidoUseCases.criarPedido(
                new CriarPedidoParam(12, "DINHEIRO"), usuarioLogado);

        assertThat(result).isEqualTo(new PedidoDetalhe(expectedPedido.withId(33), detalhesItensPedido));

        verify(pagamentoService).iniciarPagamento(expectedPedido.withId(33));
    }

    @Test
    void criarPedido_ok_clienteAnonimo() {
        var usuarioLogado = mock(UsuarioLogado.class);

        List<ItemPedido> itensPedido = List.of(
                new ItemPedido(1, 1000),
                new ItemPedido(2, 1001)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido = Map.of(
                1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger",
                        new ValorMonetario("25.90")),
                1001, new ItemCardapio(1001, TipoItemCardapio.BEBIDA, "Refrigerante", "Refrigerante",
                        new ValorMonetario("5.00"))
        );

        when(carrinhoGateway.getCarrinho(12)).thenReturn(
                new Carrinho(12, null, "Roberto Carlos",
                        itensPedido,
                        "Lanche sem cebola",
                        LocalDateTime.of(2024, 5, 18, 14, 0))
        );

        when(clock.localDateTime()).thenReturn(dateTime);
        when(pagamentoService.validarFormaPagamento("DINHEIRO")).thenReturn(FORMA_PAGAMENTO_DINHEIRO);

        when(catalogoProdutosLocal.findAll(itensPedido)).thenReturn(detalhesItensPedido);

        var expectedPedido = Pedido.novoPedido(null, "Roberto Carlos", itensPedido,
                "Lanche sem cebola", FORMA_PAGAMENTO_DINHEIRO, dateTime);

        when(pedidoGateway.savePedido(expectedPedido)).thenReturn(expectedPedido.withId(33));

        var result = pedidoUseCases.criarPedido(
                new CriarPedidoParam(12, "DINHEIRO"), usuarioLogado);

        assertThat(result).isEqualTo(new PedidoDetalhe(expectedPedido.withId(33), detalhesItensPedido));

        verify(pagamentoService).iniciarPagamento(expectedPedido.withId(33));
    }

    @Test
    void criarPedido_clienteLogadoIncorreto() {
        var usuarioLogado = mockUsuarioLogado("12332112340");

        when(clienteGateway.getClienteByCpf(new Cpf("12332112340"))).thenReturn(new Cliente(new IdCliente(42),
                "Outro Cliente", new Cpf("12332112340"), "outro.cliente@example.com"));

        when(carrinhoGateway.getCarrinho(12)).thenReturn(
                Carrinho.carrinhoSalvoClienteIdentificado(12, new IdCliente(25),
                        List.of(new ItemPedido(1, 1000)),
                        "Lanche sem cebola",
                        LocalDateTime.of(2024, 5, 18, 14, 0))
        );

        when(pagamentoService.validarFormaPagamento("DINHEIRO")).thenReturn(FORMA_PAGAMENTO_DINHEIRO);

        assertThat(
                assertThrows(DomainPermissionException.class, () -> pedidoUseCases.criarPedido(
                        new CriarPedidoParam(12, "DINHEIRO"), usuarioLogado))
        ).hasMessage("Carrinho 12 não pertence ao cliente 42/12332112340");
    }

    @Test
    void criarPedido_usuarioNaoLogado() {
        var usuarioLogado = mockUsuarioLogado(null);

        when(carrinhoGateway.getCarrinho(12)).thenReturn(
                Carrinho.carrinhoSalvoClienteIdentificado(12, new IdCliente(25),
                        List.of(new ItemPedido(1, 1000)),
                        "Lanche sem cebola",
                        LocalDateTime.of(2024, 5, 18, 14, 0))
        );

        when(pagamentoService.validarFormaPagamento("DINHEIRO")).thenReturn(FORMA_PAGAMENTO_DINHEIRO);

        assertThat(
                assertThrows(DomainPermissionException.class, () -> pedidoUseCases.criarPedido(
                        new CriarPedidoParam(12, "DINHEIRO"), usuarioLogado))
        ).hasMessageContaining("Usuário não autenticado, carrinho pertence a usuário cadastrado");
    }

    @Test
    void criarPedido_clienteNaoEncontrado() {
        var usuarioLogado = mockUsuarioLogado("12332112340");

        when(carrinhoGateway.getCarrinho(12)).thenReturn(
                Carrinho.carrinhoSalvoClienteIdentificado(12, new IdCliente(25),
                        List.of(new ItemPedido(1, 1000)),
                        "Lanche sem cebola",
                        LocalDateTime.of(2024, 5, 18, 14, 0))
        );

        when(clienteGateway.getClienteByCpf(new Cpf("12332112340"))).thenReturn(null);

        when(pagamentoService.validarFormaPagamento("DINHEIRO")).thenReturn(FORMA_PAGAMENTO_DINHEIRO);

        assertThat(
                assertThrows(IllegalStateException.class, () -> pedidoUseCases.criarPedido(
                        new CriarPedidoParam(12, "DINHEIRO"), usuarioLogado))
        ).hasMessageContaining("Registro inconsistente! Usuario logado [12332112340] não cadastrado na base");
    }

    @Test
    void criarPedido_carrinhoNulo() {
        when(carrinhoGateway.getCarrinho(12)).thenReturn(null);

        assertThatThrownBy(() -> pedidoUseCases.criarPedido(
                new CriarPedidoParam(12, "DINHEIRO"), mock()))
                .hasMessageContaining("Invalid idCarrinho");
    }

    @Test
    void validarPedido() {
        List<ItemPedido> itensPedido = List.of(
                new ItemPedido(1, 1000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido = Map.of(
                1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger",
                        new ValorMonetario("25.90"))
        );

        var pedido = Pedido.pedidoRecuperado(42, new IdCliente(25), null,
                itensPedido, "Lanche sem cebola", StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        when(pedidoGateway.getPedido(42)).thenReturn(pedido);

        when(catalogoProdutosLocal.findAll(itensPedido)).thenReturn(detalhesItensPedido);

        var expectedNewPedido = Pedido.pedidoRecuperado(42, new IdCliente(25), null,
                itensPedido, "Lanche sem cebola", StatusPedido.PREPARACAO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        ///
        var newPedido = pedidoUseCases.validarPedido(42);

        assertThat(newPedido).isEqualTo(new PedidoDetalhe(expectedNewPedido, detalhesItensPedido));

        verify(pedidoGateway).updateStatus(expectedNewPedido);
    }

    @Test
    void validarPedido_notFound() {
        when(pedidoGateway.getPedido(42)).thenReturn(null);

        assertThatThrownBy(() -> pedidoUseCases.validarPedido(42))
                .hasMessageContaining("Pedido não encontrado");
    }

    @Test
    void cancelarPedido() {
        List<ItemPedido> itensPedido = List.of(
                new ItemPedido(1, 1000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido = Map.of(
                1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger",
                        new ValorMonetario("25.90"))
        );

        var pedido = Pedido.pedidoRecuperado(42, new IdCliente(25), null,
                itensPedido, "Lanche sem cebola", StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        when(pedidoGateway.getPedido(42)).thenReturn(pedido);

        when(catalogoProdutosLocal.findAll(itensPedido)).thenReturn(detalhesItensPedido);

        var expectedNewPedido = Pedido.pedidoRecuperado(42, new IdCliente(25), null,
                itensPedido, "Lanche sem cebola", StatusPedido.CANCELADO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        ///
        var newPedido = pedidoUseCases.cancelarPedido(42);

        assertThat(newPedido).isEqualTo(new PedidoDetalhe(expectedNewPedido, detalhesItensPedido));

        verify(pedidoGateway).updateStatus(expectedNewPedido);
    }

    @Test
    void setPedidoPronto() {
        List<ItemPedido> itensPedido = List.of(
                new ItemPedido(1, 1000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido = Map.of(
                1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger",
                        new ValorMonetario("25.90"))
        );

        var pedido = Pedido.pedidoRecuperado(45, new IdCliente(25), null,
                itensPedido, "Lanche sem cebola", StatusPedido.PREPARACAO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        when(pedidoGateway.getPedido(45)).thenReturn(pedido);
        when(catalogoProdutosLocal.findAll(itensPedido)).thenReturn(detalhesItensPedido);

        var expectedNewPedido = Pedido.pedidoRecuperado(45, new IdCliente(25), null,
                itensPedido, "Lanche sem cebola",
                StatusPedido.PRONTO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        ///
        var newPedido = pedidoUseCases.setPronto(45);

        assertThat(newPedido).isEqualTo(new PedidoDetalhe(expectedNewPedido, detalhesItensPedido));

        verify(pedidoGateway).updateStatus(expectedNewPedido);
        verify(painelPedidos).notificarPedidoPronto(expectedNewPedido);
    }

    @Test
    void finalizarPedido() {
        List<ItemPedido> itensPedido = List.of(
                new ItemPedido(1, 1000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido = Map.of(
                1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger",
                        new ValorMonetario("25.90"))
        );

        var pedido = Pedido.pedidoRecuperado(42, new IdCliente(25), null,
                itensPedido, "Lanche sem cebola", StatusPedido.PRONTO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        when(pedidoGateway.getPedido(42)).thenReturn(pedido);

        when(catalogoProdutosLocal.findAll(itensPedido)).thenReturn(detalhesItensPedido);

        var expectedNewPedido = Pedido.pedidoRecuperado(42, new IdCliente(25), null,
                itensPedido, "Lanche sem cebola", StatusPedido.FINALIZADO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        ///
        var newPedido = pedidoUseCases.finalizarPedido(42);

        assertThat(newPedido).isEqualTo(new PedidoDetalhe(expectedNewPedido, detalhesItensPedido));

        verify(pedidoGateway).updateStatus(expectedNewPedido);
    }

    @Test
    void listarPedidos_byStatus() {
        List<ItemPedido> itensPedido1 = List.of(
                new ItemPedido(1, 1000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido1 = Map.of(
                1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger",
                        new ValorMonetario("25.90"))
        );

        List<ItemPedido> itensPedido2 = List.of(
                new ItemPedido(1, 2000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido2 = Map.of(
                2000, new ItemCardapio(2000, TipoItemCardapio.LANCHE, "Cheeseburger", "Cheeseburger",
                        new ValorMonetario("27.90"))
        );

        Pedido pedido1 = Pedido.pedidoRecuperado(42, new IdCliente(25), null,
                itensPedido1, "Lanche sem cebola", StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);
        Pedido pedido2 = Pedido.pedidoRecuperado(43, null, "Cliente Maria",
                itensPedido2, null, StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        when(pedidoGateway.listPedidos(List.of(StatusPedido.RECEBIDO), null)).thenReturn(List.of(
                pedido1,
                pedido2
        ));

        when(catalogoProdutosLocal.findAll(itensPedido1)).thenReturn(detalhesItensPedido1);
        when(catalogoProdutosLocal.findAll(itensPedido2)).thenReturn(detalhesItensPedido2);

        ///
        var result = pedidoUseCases.listarPedidosByStatus(StatusPedido.RECEBIDO);

        assertThat(result).containsExactly(
                new PedidoDetalhe(pedido1, detalhesItensPedido1),
                new PedidoDetalhe(pedido2, detalhesItensPedido2)
        );
    }

    @Test
    void listarPedidos_allAtivos() {
        List<ItemPedido> itensPedido1 = List.of(
                new ItemPedido(1, 1000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido1 = Map.of(
                1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger",
                        new ValorMonetario("25.90"))
        );

        List<ItemPedido> itensPedido2 = List.of(
                new ItemPedido(1, 2000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido2 = Map.of(
                2000, new ItemCardapio(2000, TipoItemCardapio.LANCHE, "Cheeseburger", "Cheeseburger",
                        new ValorMonetario("27.90"))
        );

        Pedido pedido1 = Pedido.pedidoRecuperado(42, new IdCliente(25), null,
                itensPedido1, "Lanche sem cebola", StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);
        Pedido pedido2 = Pedido.pedidoRecuperado(43, null, "Cliente Maria",
                itensPedido2, null, StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        when(pedidoGateway.listPedidos(List.of(StatusPedido.PAGAMENTO, StatusPedido.RECEBIDO, StatusPedido.PREPARACAO, StatusPedido.PRONTO), null)).thenReturn(List.of(
                pedido1,
                pedido2
        ));

        when(catalogoProdutosLocal.findAll(itensPedido1)).thenReturn(detalhesItensPedido1);
        when(catalogoProdutosLocal.findAll(itensPedido2)).thenReturn(detalhesItensPedido2);

        ///
        var result = pedidoUseCases.listarPedidosAtivos();

        assertThat(result).containsExactly(
                new PedidoDetalhe(pedido1, detalhesItensPedido1),
                new PedidoDetalhe(pedido2, detalhesItensPedido2)
        );
    }

    @Test
    void listarPedidos_byStatus_missingArgument() {
        assertThatThrownBy(() -> pedidoUseCases.listarPedidosByStatus(null))
                .hasMessageContaining("Obrigatório informar um filtro");
    }

    @Test
    void listarPedidos_comAtraso() {
        when(clock.localDateTime()).thenReturn(LocalDateTime.of(
                2024, 5, 18, 10, 40, 28
        ));

        var expectedLimitTime = LocalDateTime.of(
                2024, 5, 18, 10, 20, 28
        );

        List<ItemPedido> itensPedido1 = List.of(
                new ItemPedido(1, 1000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido1 = Map.of(
                1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger",
                        new ValorMonetario("25.90"))
        );

        List<ItemPedido> itensPedido2 = List.of(
                new ItemPedido(1, 2000)
        );

        Map<Integer, ItemCardapio> detalhesItensPedido2 = Map.of(
                2000, new ItemCardapio(2000, TipoItemCardapio.LANCHE, "Cheeseburger", "Cheeseburger",
                        new ValorMonetario("27.90"))
        );

        Pedido pedido1 = Pedido.pedidoRecuperado(42, new IdCliente(25), null,
                itensPedido1, "Lanche sem cebola", StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);
        Pedido pedido2 = Pedido.pedidoRecuperado(43, null, "Cliente Maria",
                itensPedido2, null, StatusPedido.PREPARACAO,
                FORMA_PAGAMENTO_DINHEIRO, dateTime);

        when(pedidoGateway.listPedidos(List.of(StatusPedido.RECEBIDO, StatusPedido.PREPARACAO), expectedLimitTime))
                .thenReturn(List.of(pedido1, pedido2));

        when(catalogoProdutosLocal.findAll(itensPedido1)).thenReturn(detalhesItensPedido1);
        when(catalogoProdutosLocal.findAll(itensPedido2)).thenReturn(detalhesItensPedido2);

        var result = pedidoUseCases.listarPedidosComAtraso();

        assertThat(result).containsExactly(
                new PedidoDetalhe(pedido1, detalhesItensPedido1),
                new PedidoDetalhe(pedido2, detalhesItensPedido2)
        );
    }

    private UsuarioLogado mockUsuarioLogado(String cpf) {
        UsuarioLogado usuarioLogado = mock(UsuarioLogado.class);
        if (cpf == null) {
            when(usuarioLogado.autenticado()).thenReturn(false);
        } else {
            when(usuarioLogado.getCpf()).thenReturn(cpf);
            when(usuarioLogado.autenticado()).thenReturn(true);
        }
        return usuarioLogado;
    }

    ///////////
    private final LocalDateTime dateTime = LocalDateTime.of(2024, 5, 18, 15, 30);

    private static final IdFormaPagamento FORMA_PAGAMENTO_DINHEIRO = new IdFormaPagamento("DINHEIRO");
}
