package com.example.fiap.archburgers.domain.usecases;

import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.entities.Carrinho;
import com.example.fiap.archburgers.domain.entities.Cliente;
import com.example.fiap.archburgers.domain.entities.ItemCardapio;
import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosLocal;
import com.example.fiap.archburgers.domain.usecaseparam.CriarCarrinhoParam;
import com.example.fiap.archburgers.domain.utils.Clock;
import com.example.fiap.archburgers.domain.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarrinhoUseCasesTest {
    @Mock
    private CarrinhoGateway carrinhoGateway;
    @Mock
    private ClienteGateway clienteGateway;
    @Mock
    private CatalogoProdutosLocal catalogoProdutosLocal;

    @Mock
    private Clock clock;

    private CarrinhoUseCases carrinhoUseCases;

    @BeforeEach
    void setUp() {
        carrinhoUseCases = new CarrinhoUseCases(carrinhoGateway, clienteGateway, catalogoProdutosLocal, clock);
    }

    @Test
    void criarCarrinho_clienteIdentificado_carrinhoExistente() {
        when(clienteGateway.getClienteByCpf(new Cpf("12332112340"))).thenReturn(cliente123);

        when(carrinhoGateway.getCarrinhoSalvoByCliente(new IdCliente(123))).thenReturn(carrinhoSalvoCliente123);

        when(catalogoProdutosLocal.findAll(List.of(new ItemPedido(1, 1000)))).thenReturn(
                Map.of(1000, new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger", new ValorMonetario("25.90")))
        );

        UsuarioLogado usuarioLogado = mockUsuarioLogado("12332112340");

        var result = carrinhoUseCases.criarCarrinho(new CriarCarrinhoParam(null), usuarioLogado);

        assertThat(result).isEqualTo(new CarrinhoDetalhe(carrinhoSalvoCliente123,
                Map.of(1000, item1000))
        );
    }

    @Test
    void criarCarrinho_clienteIdentificado_novoCarrinho() throws Exception {
        when(clienteGateway.getClienteByCpf(new Cpf("12332112340"))).thenReturn(cliente123);

        when(carrinhoGateway.getCarrinhoSalvoByCliente(new IdCliente(123))).thenReturn(null);
        when(clock.localDateTime()).thenReturn(dateTime);

        when(carrinhoGateway.salvarCarrinhoVazio(carrinhoVazioCliente123)).thenReturn(
                carrinhoVazioCliente123.withId(99));

        UsuarioLogado usuarioLogado = mockUsuarioLogado("12332112340");

        var result = carrinhoUseCases.criarCarrinho(new CriarCarrinhoParam(null), usuarioLogado);
        assertThat(result).isEqualTo(new CarrinhoDetalhe(carrinhoVazioCliente123.withId(99), Collections.emptyMap()));
    }

    @Test
    void criarCarrinho_clienteNaoIdentificado_novoCarrinho() throws Exception {
        when(clock.localDateTime()).thenReturn(dateTime);

        when(carrinhoGateway.salvarCarrinhoVazio(carrinhoNaoIdentificado)).thenReturn(
                carrinhoNaoIdentificado.withId(101));

        UsuarioLogado usuarioNaoLogado = mockUsuarioLogado(null);

        var result = carrinhoUseCases.criarCarrinho(new CriarCarrinhoParam("João"), usuarioNaoLogado);
        assertThat(result).isEqualTo(new CarrinhoDetalhe(carrinhoNaoIdentificado.withId(101), Collections.emptyMap()));
    }

    @Test
    void criarCarrinho_clienteNaoIdentificado_erroNomeNaoInformado() throws Exception {
        UsuarioLogado usuarioNaoLogado = mockUsuarioLogado(null);

        assertThat(
                assertThrows(DomainArgumentException.class, () ->
                        carrinhoUseCases.criarCarrinho(new CriarCarrinhoParam(""), usuarioNaoLogado))
        ).hasMessage("Cliente não autenticado deve informar o nomeCliente");
    }

    @Test
    void addItemCarrinho() {
        Carrinho carrinhoInicial = Carrinho.carrinhoSalvoClienteIdentificado(
                88, new IdCliente(123), List.of(
                        new ItemPedido(1, 1000),
                        new ItemPedido(2, 1001)
                ), null, dateTime);

        when(carrinhoGateway.getCarrinho(88)).thenReturn(carrinhoInicial);

        when(catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1000),
                new ItemPedido(2, 1001),
                new ItemPedido(3, 1002)
        ))).thenReturn(
                Map.of(1000, item1000, 1001, item1001, 1002, item1002)
        );

        when(catalogoProdutosLocal.findById(1002)).thenReturn(item1002);

        //
        var newCarrinho = carrinhoUseCases.addItem(88, 1002);

        Carrinho expectedCarrinhoFinal = Carrinho.carrinhoSalvoClienteIdentificado(
                88, new IdCliente(123), List.of(
                        new ItemPedido(1, 1000),
                        new ItemPedido(2, 1001),
                        new ItemPedido(3, 1002)
                ), null, dateTime);


        assertThat(newCarrinho).isEqualTo(new CarrinhoDetalhe(expectedCarrinhoFinal,
                Map.of(1000, item1000, 1001, item1001, 1002, item1002)));

        verify(carrinhoGateway).salvarItemCarrinho(expectedCarrinhoFinal,
                new ItemPedido(3, 1002));
    }

    @Test
    void deleteItem() {
        Carrinho carrinhoInicial = Carrinho.carrinhoSalvoClienteIdentificado(
                88, new IdCliente(123), List.of(
                        new ItemPedido(1, 1000),
                        new ItemPedido(2, 1001),
                        new ItemPedido(3, 1002)
                ), null, dateTime);

        when(carrinhoGateway.getCarrinho(88)).thenReturn(carrinhoInicial);

        List<ItemPedido> expectedItensFinal = List.of(
                new ItemPedido(1, 1000),
                new ItemPedido(2, 1002)
        );

        when(catalogoProdutosLocal.findAll(expectedItensFinal)).thenReturn(Map.of(
                1000, item1000, 1002, item1002
        ));

        ///
        var updated = carrinhoUseCases.deleteItem(88, 2);

        Carrinho expectedNewCarrinho = Carrinho.carrinhoSalvoClienteIdentificado(
                88, new IdCliente(123), expectedItensFinal, null, dateTime);

        assertThat(updated).isEqualTo(new CarrinhoDetalhe(expectedNewCarrinho, Map.of(
                1000, item1000, 1002, item1002
        )));

        verify(carrinhoGateway).deleteItensCarrinho(expectedNewCarrinho);
        verify(carrinhoGateway).salvarItemCarrinho(expectedNewCarrinho, new ItemPedido(1, 1000));
        verify(carrinhoGateway).salvarItemCarrinho(expectedNewCarrinho, new ItemPedido(2, 1002));
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

    // // // Predefined test objects
    private final Cliente cliente123 = new Cliente(new IdCliente(123), "Cliente", new Cpf("12332112340"), "cliente123@example.com");

    private final LocalDateTime dateTime = LocalDateTime.of(2024, 4, 29, 15, 30);

    private final Carrinho carrinhoSalvoCliente123 = Carrinho.carrinhoSalvoClienteIdentificado(
            88, new IdCliente(123), List.of(new ItemPedido(1, 1000)), null, dateTime);

    private final Carrinho carrinhoNaoIdentificado = Carrinho.newCarrinhoVazioClienteNaoIdentificado("João", dateTime);

    private final Carrinho carrinhoVazioCliente123 = Carrinho.newCarrinhoVazioClienteIdentificado(new IdCliente(123), dateTime);

    private final ItemCardapio item1000 = new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger", new ValorMonetario("25.90"));
    private final ItemCardapio item1001 = new ItemCardapio(1001, TipoItemCardapio.BEBIDA, "Refrigerante", "Refrigerante", new ValorMonetario("5.00"));
    private final ItemCardapio item1002 = new ItemCardapio(1002, TipoItemCardapio.SOBREMESA, "Sundae", "Sundae", new ValorMonetario("9.40"));
}