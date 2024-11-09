package com.example.fiap.archburgers.domain.valueobjects;//import static org.junit.jupiter.api.Assertions.*;

import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.entities.Pedido;
import com.example.fiap.archburgers.domain.external.ItemCardapio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PedidoDetalheTest {

    @Test
    void getValorTotal_pedidoVazio() {
        Pedido pedido = mockPedidoWithItens(Collections.emptyList());
        PedidoDetalhe pedidoDetalhe = new PedidoDetalhe(pedido, new HashMap<>());
        assertThat(pedidoDetalhe.getValorTotal()).isEqualTo(new ValorMonetario(BigDecimal.ZERO));
    }

    @Test
    void getValorTotal_unicoItem() {
        Pedido pedido = mockPedidoWithItens(List.of(SAMPLE_ITEM_1));
        Map<Integer, ItemCardapio> detalhesItens = Map.of(SAMPLE_ITEM_1.id(), SAMPLE_ITEM_1);
        PedidoDetalhe pedidoDetalhe = new PedidoDetalhe(pedido, detalhesItens);
        ValorMonetario expected = new ValorMonetario("25.00");
        assertThat(pedidoDetalhe.getValorTotal()).isEqualTo(expected);
    }

    @Test
    void getValorTotal_invalidMap() {
        Pedido pedido = mockPedidoWithItens(List.of(SAMPLE_ITEM_1));

        PedidoDetalhe pedidoDetalhe = new PedidoDetalhe(pedido, Collections.emptyMap());

        assertThatThrownBy(pedidoDetalhe::getValorTotal)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("Dados de Pedido inconsistentes");
    }

    @Test
    void getValorTotal_somaMultiplosItens() {
        Pedido pedido = mockPedidoWithItens(List.of(SAMPLE_ITEM_1, SAMPLE_ITEM_2));

        Map<Integer, ItemCardapio> detalhesItens = Map.of(
                SAMPLE_ITEM_1.id(), SAMPLE_ITEM_1, SAMPLE_ITEM_2.id(), SAMPLE_ITEM_2);

        PedidoDetalhe pedidoDetalhe = new PedidoDetalhe(pedido, detalhesItens);

        assertThat(pedidoDetalhe.getValorTotal()).isEqualTo(new ValorMonetario("32.99"));
    }


    @Test
    void getValorTotal_contemItemRepetido() {
        Pedido pedido = mockPedidoWithItens(List.of(SAMPLE_ITEM_1, SAMPLE_ITEM_2, SAMPLE_ITEM_2, SAMPLE_ITEM_1));

        Map<Integer, ItemCardapio> detalhesItens = Map.of(
                SAMPLE_ITEM_1.id(), SAMPLE_ITEM_1, SAMPLE_ITEM_2.id(), SAMPLE_ITEM_2);

        PedidoDetalhe pedidoDetalhe = new PedidoDetalhe(pedido, detalhesItens);

        assertThat(pedidoDetalhe.getValorTotal()).isEqualTo(new ValorMonetario("65.98"));
    }


    private Pedido mockPedidoWithItens(List<ItemCardapio> itens) {
        Pedido pedido = mock();

        List<ItemPedido> itensPedido = new ArrayList<>();
        for (int i = 0; i < itens.size(); i++) {
            ItemCardapio item = itens.get(i);

            itensPedido.add(new ItemPedido(i + 1, item.id()));
        }

        when(pedido.itens()).thenReturn(itensPedido);

        return pedido;
    }

    private static final ItemCardapio SAMPLE_ITEM_1 = new ItemCardapio(1001, TipoItemCardapio.LANCHE,
            "Hamburger", "Hamburger", new ValorMonetario("25.00"));

    private static final ItemCardapio SAMPLE_ITEM_2 = new ItemCardapio(1002, TipoItemCardapio.LANCHE,
            "Coca cola", "Coca cola", new ValorMonetario("7.99"));
}