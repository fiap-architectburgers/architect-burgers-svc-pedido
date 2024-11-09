package com.example.fiap.archburgers.domain.valueobjects;

import com.example.fiap.archburgers.domain.entities.Carrinho;
import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.external.ItemCardapio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CarrinhoDetalheTest {

    @Test
    void getValorTotal_carrinhoVazio() {
        Carrinho carrinho = mockCarrinhoWithItens(Collections.emptyList());
        CarrinhoDetalhe carrinhoDetalhe = new CarrinhoDetalhe(carrinho, new HashMap<>());
        assertThat(carrinhoDetalhe.getValorTotal()).isEqualTo(new ValorMonetario(BigDecimal.ZERO));
    }

    @Test
    void getValorTotal_unicoItem() {
        Carrinho carrinho = mockCarrinhoWithItens(List.of(SAMPLE_ITEM_1));
        Map<Integer, ItemCardapio> detalhesItens = Map.of(SAMPLE_ITEM_1.id(), SAMPLE_ITEM_1);
        CarrinhoDetalhe carrinhoDetalhe = new CarrinhoDetalhe(carrinho, detalhesItens);
        ValorMonetario expected = new ValorMonetario("25.00");
        assertThat(carrinhoDetalhe.getValorTotal()).isEqualTo(expected);
    }

    @Test
    void getValorTotal_invalidMap() {
        Carrinho carrinho = mockCarrinhoWithItens(List.of(SAMPLE_ITEM_1));

        CarrinhoDetalhe carrinhoDetalhe = new CarrinhoDetalhe(carrinho, Collections.emptyMap());

        assertThatThrownBy(carrinhoDetalhe::getValorTotal)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageStartingWith("Dados de Carrinho inconsistentes");
    }

    @Test
    void getValorTotal_somaMultiplosItens() {
        Carrinho carrinho = mockCarrinhoWithItens(List.of(SAMPLE_ITEM_1, SAMPLE_ITEM_2));

        Map<Integer, ItemCardapio> detalhesItens = Map.of(
                SAMPLE_ITEM_1.id(), SAMPLE_ITEM_1, SAMPLE_ITEM_2.id(), SAMPLE_ITEM_2);

        CarrinhoDetalhe carrinhoDetalhe = new CarrinhoDetalhe(carrinho, detalhesItens);

        assertThat(carrinhoDetalhe.getValorTotal()).isEqualTo(new ValorMonetario("32.99"));
    }


    @Test
    void getValorTotal_contemItemRepetido() {
        Carrinho carrinho = mockCarrinhoWithItens(List.of(SAMPLE_ITEM_1, SAMPLE_ITEM_2, SAMPLE_ITEM_2, SAMPLE_ITEM_1));

        Map<Integer, ItemCardapio> detalhesItens = Map.of(
                SAMPLE_ITEM_1.id(), SAMPLE_ITEM_1, SAMPLE_ITEM_2.id(), SAMPLE_ITEM_2);

        CarrinhoDetalhe carrinhoDetalhe = new CarrinhoDetalhe(carrinho, detalhesItens);

        assertThat(carrinhoDetalhe.getValorTotal()).isEqualTo(new ValorMonetario("65.98"));
    }


    private Carrinho mockCarrinhoWithItens(List<ItemCardapio> itens) {
        Carrinho carrinho = mock();

        List<ItemPedido> itensCarrinho = new ArrayList<>();
        for (int i = 0; i < itens.size(); i++) {
            ItemCardapio item = itens.get(i);

            itensCarrinho.add(new ItemPedido(i + 1, item.id()));
        }

        when(carrinho.itens()).thenReturn(itensCarrinho);

        return carrinho;
    }

    private static final ItemCardapio SAMPLE_ITEM_1 = new ItemCardapio(1001, TipoItemCardapio.LANCHE,
            "Hamburger", "Hamburger", new ValorMonetario("25.00"));

    private static final ItemCardapio SAMPLE_ITEM_2 = new ItemCardapio(1002, TipoItemCardapio.LANCHE,
            "Coca cola", "Coca cola", new ValorMonetario("7.99"));
}