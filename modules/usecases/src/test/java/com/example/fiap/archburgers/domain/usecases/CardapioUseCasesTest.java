package com.example.fiap.archburgers.domain.usecases;

import com.example.fiap.archburgers.domain.entities.ItemCardapio;
import com.example.fiap.archburgers.domain.datagateway.ItemCardapioGateway;
import com.example.fiap.archburgers.domain.valueobjects.TipoItemCardapio;
import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardapioUseCasesTest {
    @Mock
    private ItemCardapioGateway itemCardapioGateway;

    private CardapioUseCases cardapioUseCases;

    @BeforeEach
    void setUp() {
        cardapioUseCases = new CardapioUseCases(itemCardapioGateway);
    }

    @Test
    void listLanches() {
        when(itemCardapioGateway.findAll()).thenReturn(List.of(
                new ItemCardapio(1, TipoItemCardapio.LANCHE, "Hamburger Vegetariano",
                        "Hamburger de ervilha com queijo prato",
                        new ValorMonetario("20.00")),
                new ItemCardapio(2, TipoItemCardapio.LANCHE, "Veggie Cheddar",
                        "Hamburger do Futuro com cebolas caramelizadas e cheddar vegano",
                        new ValorMonetario("25.00"))
        ));

        var result = cardapioUseCases.listarItensCardapio(null);

        assertThat(result).containsExactly(
                new ItemCardapio(1, TipoItemCardapio.LANCHE, "Hamburger Vegetariano",
                        "Hamburger de ervilha com queijo prato",
                        new ValorMonetario("20.00")),
                new ItemCardapio(2, TipoItemCardapio.LANCHE, "Veggie Cheddar",
                        "Hamburger do Futuro com cebolas caramelizadas e cheddar vegano",
                        new ValorMonetario("25.00"))
        );
    }
}