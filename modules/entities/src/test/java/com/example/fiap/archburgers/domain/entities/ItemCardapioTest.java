package com.example.fiap.archburgers.domain.entities;

import com.example.fiap.archburgers.domain.valueobjects.TipoItemCardapio;
import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ItemCardapioTest {

    private ItemCardapio itemCardapio;

    @BeforeEach
    void setUp() {
        itemCardapio = new ItemCardapio(21, TipoItemCardapio.LANCHE, "Hamburger Vegetariano",
                "Hamburger de ervilha com queijo vegano",
                new ValorMonetario("23.50"));
    }

    @Test
    void checkAttributes() {
        assertThat(itemCardapio.id()).isEqualTo(21);
        assertThat(itemCardapio.tipo()).isEqualTo(TipoItemCardapio.LANCHE);
        assertThat(itemCardapio.nome()).isEqualTo("Hamburger Vegetariano");
        assertThat(itemCardapio.descricao()).isEqualTo("Hamburger de ervilha com queijo vegano");
        assertThat(itemCardapio.valor()).isEqualTo(new ValorMonetario(new BigDecimal("23.50")));
    }

    @Test
    void somarValores_empty() {
        assertThat(ItemCardapio.somarValores(null)).isEqualTo(new ValorMonetario(BigDecimal.ZERO));
        assertThat(ItemCardapio.somarValores(Collections.emptyList())).isEqualTo(new ValorMonetario(BigDecimal.ZERO));
    }

    @Test
    void somarValores_one(){
        assertThat(ItemCardapio.somarValores(List.of(
                createDummyItemWithValor("23.50"))
        )).isEqualTo(new ValorMonetario(new BigDecimal("23.50")));
    }

    @Test
    void somarValores_many(){
        assertThat(ItemCardapio.somarValores(List.of(
                createDummyItemWithValor("23.50"),
                createDummyItemWithValor("9.9"),
                createDummyItemWithValor(".85"),
                createDummyItemWithValor("12")
        ))).isEqualTo(new ValorMonetario(new BigDecimal("46.25")));
    }

    private ItemCardapio createDummyItemWithValor(String valorStr) {
        return new ItemCardapio(1, TipoItemCardapio.LANCHE, "Dummy",
                "Dummy",
                new ValorMonetario(valorStr));
    }
}