package com.example.fiap.archburgers.domain.entities;

import com.example.fiap.archburgers.domain.external.ItemCardapio;
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

    @Test
    void assignId() {
        var carrinho = initial();

        Carrinho carrinhoWithId = carrinho.withId(10);

        assertThat(carrinhoWithId.id()).isEqualTo(10);
    }

    @Test
    void adicionarItem() {
        var carrinho = initial();

        ItemCardapio itemCardapio = new ItemCardapio(10010, TipoItemCardapio.LANCHE, "Test Item",
                "Test Item Desc", new ValorMonetario("1.50"));
        Carrinho carrinhoWithNewItem = carrinho.adicionarItem(itemCardapio);

        assertThat(carrinhoWithNewItem.itens()).contains(new ItemPedido(4, 10010));
    }

    @Test
    void setObservacao() {
        var carrinho = initial();

        Carrinho updated = carrinho.setObservacoes("Observação do cliente");

        assertThat(updated.id()).isEqualTo(12);
        assertThat(updated.idClienteIdentificado()).isEqualTo(carrinho.idClienteIdentificado());
        assertThat(updated.itens()).isEqualTo(carrinho.itens());
        assertThat(updated.observacoes()).isEqualTo("Observação do cliente");
    }

    @Test
    void newCarrinhoVazioClienteIdentificado() {
        Carrinho result = Carrinho.newCarrinhoVazioClienteIdentificado(new IdCliente(123),
        LocalDateTime.of(2024, 4, 29, 15, 30));

        assertThat(result.id()).isNull();
        assertThat(result.idClienteIdentificado()).isEqualTo(new IdCliente(123));
        assertThat(result.itens()).isEmpty();
        assertThat(result.observacoes()).isNull();
        assertThat(result.dataHoraCarrinhoCriado()).isEqualTo(LocalDateTime.of(2024, 4, 29, 15, 30));
    }

    @Test
    void newCarrinhoVazioClienteNaoIdentificado() {
        Carrinho result = Carrinho.newCarrinhoVazioClienteNaoIdentificado("Custommer X",
                LocalDateTime.of(2024, 4, 29, 15, 30));

        assertThat(result.id()).isNull();
        assertThat(result.idClienteIdentificado()).isNull();
        assertThat(result.nomeClienteNaoIdentificado()).isEqualTo("Custommer X");
        assertThat(result.itens()).isEmpty();
        assertThat(result.observacoes()).isNull();
        assertThat(result.dataHoraCarrinhoCriado())
                .isEqualTo(LocalDateTime.of(2024, 4, 29, 15, 30));
    }


    @Test
    void equalsAndHashCode() {
        Carrinho carrinho1 = Carrinho.carrinhoSalvoClienteIdentificado(1,
                new IdCliente(123),
                List.of(new ItemPedido(1, 1001), new ItemPedido(2, 1002)),
                "Observação Pedido", LocalDateTime.of(2024, 4, 29, 15, 30));

        Carrinho carrinho2 = Carrinho.carrinhoSalvoClienteIdentificado(1,
                new IdCliente(123),
                List.of(new ItemPedido(1, 1001), new ItemPedido(2, 1002)),
                "Observação Pedido", LocalDateTime.of(2024, 4, 29, 15, 30));

        Carrinho carrinho3 = Carrinho.carrinhoSalvoClienteIdentificado(2,
                new IdCliente(124),
                List.of(new ItemPedido(1, 1001), new ItemPedido(2, 1002)),
                "Observação Pedido", LocalDateTime.of(2024, 4, 29, 15, 30));

        // Test equality and hashcode when both objects are the same (i.e. test reflexity)
        assertThat(carrinho1).isEqualTo(carrinho1);
        assertThat(carrinho1.hashCode()).isEqualTo(carrinho1.hashCode());

        // Test equality and hashcode when objects are equal (i.e. test symmetry)
        assertThat(carrinho1).isEqualTo(carrinho2);
        assertThat(carrinho1.hashCode()).isEqualTo(carrinho2.hashCode());

        // Test equality and hashcode when objects are not equal
        assertThat(carrinho1).isNotEqualTo(carrinho3);
        assertThat(carrinho1.hashCode()).isNotEqualTo(carrinho3.hashCode());

        // Test when comparing to null
        assertThat(carrinho1).isNotEqualTo(null);

        System.out.println(carrinho1.toString());
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
