package com.example.fiap.archburgers.domain.entities;

import com.example.fiap.archburgers.domain.valueobjects.IdCliente;
import com.example.fiap.archburgers.domain.valueobjects.TipoItemCardapio;
import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarrinhoTest {

    @Test
    void build_carrinhoSalvoClienteIdentificado() {
        Carrinho result = Carrinho.carrinhoSalvoClienteIdentificado(1, new IdCliente(123), "Observação Pedido", LocalDateTime.of(2024, 4, 29, 15, 30));
        assertThat(result.id()).isEqualTo(1);
        assertThat(result.idClienteIdentificado()).isEqualTo(new IdCliente(123));
        assertThat(result.observacoes()).isEqualTo("Observação Pedido");
        assertThat(result.dataHoraCarrinhoCriado()).isEqualTo(LocalDateTime.of(2024, 4, 29, 15, 30));
    }

    @Test
    void deleteItem_first() {
        var carrinho = initial();

        carrinho = carrinho.deleteItem(1);

        assertThat(carrinho.itens()).containsExactly(
                new ItemPedido(1,
                        new ItemCardapio(1008, TipoItemCardapio.BEBIDA, "Refrigerante", "Refrigerante", new ValorMonetario("5.00"))
                ),
                new ItemPedido(2,
                        new ItemCardapio(1009, TipoItemCardapio.SOBREMESA, "Sundae", "Sundae", new ValorMonetario("9.40"))
                )
        );
    }

    @Test
    void deleteItem_last() {
        var carrinho = initial();

        carrinho = carrinho.deleteItem(3);

        assertThat(carrinho.itens()).containsExactly(
                new ItemPedido(1,
                        new ItemCardapio(1007, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger", new ValorMonetario("25.90"))
                ),
                new ItemPedido(2,
                        new ItemCardapio(1008, TipoItemCardapio.BEBIDA, "Refrigerante", "Refrigerante", new ValorMonetario("5.00"))
                )
        );
    }

    @Test
    void deleteItem_middle() {
        var carrinho = initial();

        carrinho = carrinho.deleteItem(2);

        assertThat(carrinho.itens()).containsExactly(
                new ItemPedido(1,
                        new ItemCardapio(1007, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger", new ValorMonetario("25.90"))
                ),
                new ItemPedido(2,
                        new ItemCardapio(1009, TipoItemCardapio.SOBREMESA, "Sundae", "Sundae", new ValorMonetario("9.40"))
                )
        );
    }

    @Test
    void deleteItem_outOfBounds() {
        var carrinho = initial();

        assertThrows(IllegalArgumentException.class, () -> carrinho.deleteItem(4));
    }

    @Test
    void deleteItem_all() {
        var carrinho = initial();

        carrinho = carrinho.deleteItem(3);
        carrinho = carrinho.deleteItem(2);
        carrinho = carrinho.deleteItem(1);

        assertThat(carrinho.itens()).isEmpty();
    }

    private Carrinho initial() {
        return new Carrinho(12, new IdCliente(123), null, List.of(
                new ItemPedido(1,
                        new ItemCardapio(1007, TipoItemCardapio.LANCHE, "Hamburger", "Hamburger", new ValorMonetario("25.90"))
                ),
                new ItemPedido(2,
                        new ItemCardapio(1008, TipoItemCardapio.BEBIDA, "Refrigerante", "Refrigerante", new ValorMonetario("5.00"))
                ),
                new ItemPedido(3,
                        new ItemCardapio(1009, TipoItemCardapio.SOBREMESA, "Sundae", "Sundae", new ValorMonetario("9.40"))
                )
        ), null, dateTime);
    }

    private final LocalDateTime dateTime = LocalDateTime.of(2024, 4, 29, 15, 30);
}