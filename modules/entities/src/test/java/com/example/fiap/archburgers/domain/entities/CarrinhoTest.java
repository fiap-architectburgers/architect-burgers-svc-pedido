package com.example.fiap.archburgers.domain.entities;

import com.example.fiap.archburgers.domain.valueobjects.IdCliente;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CarrinhoTest {

    @Test
    void build_carrinhoSalvoClienteIdentificado() {
        Carrinho result = Carrinho.carrinhoSalvoClienteIdentificado(1, new IdCliente(123),
                List.of(new ItemPedido(1, 1001), new ItemPedido(2, 1002)),
                "Observação Pedido", LocalDateTime.of(2024, 4, 29, 15, 30));

        assertThat(result.id()).isEqualTo(1);
        assertThat(result.idClienteIdentificado()).isEqualTo(new IdCliente(123));
        assertThat(result.itens()).containsExactly(
                new ItemPedido(1, 1001), new ItemPedido(2, 1002)
        );
        assertThat(result.observacoes()).isEqualTo("Observação Pedido");
        assertThat(result.dataHoraCarrinhoCriado()).isEqualTo(LocalDateTime.of(2024, 4, 29, 15, 30));
    }

    @Test
    void deleteItem_first() {
        var carrinho = initial();

        carrinho = carrinho.deleteItem(1);

        assertThat(carrinho.itens()).containsExactly(
                new ItemPedido(1, 1008),
                new ItemPedido(2, 1009)
        );
    }

    @Test
    void deleteItem_last() {
        var carrinho = initial();

        carrinho = carrinho.deleteItem(3);

        assertThat(carrinho.itens()).containsExactly(
                new ItemPedido(1, 1007),
                new ItemPedido(2, 1008)
        );
    }

    @Test
    void deleteItem_middle() {
        var carrinho = initial();

        carrinho = carrinho.deleteItem(2);

        assertThat(carrinho.itens()).containsExactly(
                new ItemPedido(1, 1007),
                new ItemPedido(2, 1009)
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
                new ItemPedido(1, 1007),
                new ItemPedido(2, 1008),
                new ItemPedido(3, 1009)
        ), null, dateTime);
    }

    private final LocalDateTime dateTime = LocalDateTime.of(2024, 4, 29, 15, 30);
}