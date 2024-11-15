package com.example.fiap.archburgers.domain.entities;

import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.external.Pagamento;
import com.example.fiap.archburgers.domain.valueobjects.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PedidoTest {

    private final List<ItemPedido> sampleItens = List.of(
            new ItemPedido(1, 9)
    );


    @Test
    void novoPedido_ok() {
        var idCliente = new IdCliente(33);
        var itemList = List.of(new ItemPedido(1, 2), new ItemPedido(3, 4));
        var dateTime = LocalDateTime.now();

        Pedido pedido = Pedido.novoPedido(idCliente, null,
                itemList, "Test Observations", FORMA_PAGAMENTO_DINHEIRO, dateTime);

        assertThat(pedido.idClienteIdentificado()).isEqualTo(idCliente);
        assertThat(pedido.nomeClienteNaoIdentificado()).isNull();
        assertThat(pedido.itens()).containsExactlyElementsOf(itemList);
        assertThat(pedido.observacoes()).isEqualTo("Test Observations");
        assertThat(pedido.formaPagamento()).isEqualTo(FORMA_PAGAMENTO_DINHEIRO);
        assertThat(pedido.dataHoraPedido()).isEqualTo(dateTime);
    }

    @Test
    void novoPedido_emptyItemList() {
        var idCliente = new IdCliente(33);

        assertThatThrownBy(() -> Pedido.novoPedido(idCliente, "Cliente Test",
                List.of(), "Test Observations", FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now()))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pedido deve conter itens");
    }

    @Test
    void novoPedido_nullItemList() {
        var idCliente = new IdCliente(33);

        assertThatThrownBy(() -> Pedido.novoPedido(idCliente, "Cliente Test",
                null, "Test Observations", FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now()))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pedido deve conter itens");
    }

    @Test
    void validar() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        var newP = p.validar();

        assertThat(newP.status()).isEqualTo(StatusPedido.PREPARACAO);
    }

    @Test
    void assign_withId() {
        var p = Pedido.novoPedido(new IdCliente(33), null,
                List.of(new ItemPedido(1, 2), new ItemPedido(3, 4)),
                "Test Observations", FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        var newP = p.withId(123);

        assertThat(newP.id()).isEqualTo(123);
    }

    @Test
    void validar_statusInvalido() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.PRONTO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        assertThat(
                assertThrows(IllegalArgumentException.class, p::validar)
        ).hasMessage("Status invalido para validação do pedido: PRONTO");
    }

    @Test
    void cancelar() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        var newP = p.cancelar();

        assertThat(newP.status()).isEqualTo(StatusPedido.CANCELADO);
    }

    @Test
    void setPronto() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.PREPARACAO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        var newP = p.setPronto();

        assertThat(newP.status()).isEqualTo(StatusPedido.PRONTO);
    }

    @Test
    void setPronto_statusInvalido() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.RECEBIDO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        assertThrows(DomainArgumentException.class, p::setPronto);
    }

    @Test
    void finalizar() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.PRONTO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        var newP = p.finalizar();

        assertThat(newP.status()).isEqualTo(StatusPedido.FINALIZADO);
    }

    @Test
    void finalizar_statusInvalido() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.PREPARACAO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        assertThrows(DomainArgumentException.class, p::finalizar);
    }

    @Test
    void confirmarPagamento() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.PAGAMENTO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        var pagamento = new Pagamento("4455", 123,
                FORMA_PAGAMENTO_DINHEIRO, StatusPagamento.FINALIZADO,
                new ValorMonetario("19.90"), LocalDateTime.now(),
                LocalDateTime.now(), null, null);

        var newP = p.confirmarPagamento(pagamento);

        assertThat(newP.status()).isEqualTo(StatusPedido.RECEBIDO);
    }


    @Test
    void confirmarPagamento_pagamentoNull() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.PAGAMENTO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        assertThatThrownBy(() -> p.confirmarPagamento(null))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pagamento nulo");
    }

    @Test
    void confirmarPagamento_pagamentoNotGravado() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.PAGAMENTO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        var pagamento = new Pagamento(null, 123,
                FORMA_PAGAMENTO_DINHEIRO, StatusPagamento.FINALIZADO,
                new ValorMonetario("19.90"), LocalDateTime.now(),
                LocalDateTime.now(), null, null);

        assertThatThrownBy(() -> p.confirmarPagamento(pagamento))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pagamento deve estar gravado");
    }

    @Test
    void confirmarPagamento_statusInvalido() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.PRONTO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        var pagamento = new Pagamento("4455", 123,
                FORMA_PAGAMENTO_DINHEIRO, StatusPagamento.FINALIZADO,
                new ValorMonetario("19.90"), LocalDateTime.now(),
                LocalDateTime.now(), null, null);

        assertThatThrownBy(() -> p.confirmarPagamento(pagamento))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Status invalido para pagamento");
    }

    @Test
    void confirmarPagamento_pagamentoNaoFinalizado() {
        var p = Pedido.pedidoRecuperado(123, null, "Cliente José",
                sampleItens, null, StatusPedido.PAGAMENTO,
                FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now());

        var pagamento = new Pagamento("4455", 123,
                FORMA_PAGAMENTO_DINHEIRO, StatusPagamento.PENDENTE,
                new ValorMonetario("19.90"), LocalDateTime.now(),
                LocalDateTime.now(), null, null);

        assertThatThrownBy(() -> p.confirmarPagamento(pagamento))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pagamento nao esta finalizado");
    }

    @Test
    void stateValidation_nullId() {
        assertThatThrownBy(() -> Pedido.pedidoRecuperado(null,
                new IdCliente(33), null, sampleItens, "Test Observations",
                StatusPedido.FINALIZADO, FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now()))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pedido existente deve possuir id");
    }

    @Test
    void stateValidation_bothClientParametersNull() {
        assertThatThrownBy(() -> Pedido.pedidoRecuperado(1,
                null, null, sampleItens, "Test Observations",
                StatusPedido.FINALIZADO, FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now()))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pedido deve ter idClienteIdentificado ou nomeClienteNaoIdentificado");
    }

    @Test
    void stateValidation_bothClientParametersInformed() {
        assertThatThrownBy(() -> Pedido.pedidoRecuperado(1,
                new IdCliente(33), "Test Client", sampleItens, "Test Observations",
                StatusPedido.FINALIZADO, FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now()))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pedido nao pode ter ambos idClienteIdentificado e nomeClienteNaoIdentificado");
    }

    @Test
    void stateValidation_nullStatus() {
        assertThatThrownBy(() -> Pedido.pedidoRecuperado(1,
                new IdCliente(33), null, sampleItens, "Test Observations",
                null, FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now()))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pedido deve conter status");
    }

    @Test
    void stateValidation_nullFormaPagamento() {
        assertThatThrownBy(() -> Pedido.pedidoRecuperado(1,
                new IdCliente(33), null, sampleItens, "Test Observations",
                StatusPedido.FINALIZADO, null, LocalDateTime.now()))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pedido deve conter formaPagamento");
    }

    @Test
    void stateValidation_nullDataHoraPedido() {
        assertThatThrownBy(() -> Pedido.pedidoRecuperado(1,
                new IdCliente(33), null, sampleItens, "Test Observations",
                StatusPedido.FINALIZADO, FORMA_PAGAMENTO_DINHEIRO, null))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Pedido deve conter dataHoraPedido");
    }

    @Test
    void stateValidation_nullItens() {
        assertThatThrownBy(() -> Pedido.pedidoRecuperado(1,
                new IdCliente(33), null, null, "Test Observations",
                StatusPedido.FINALIZADO, FORMA_PAGAMENTO_DINHEIRO, LocalDateTime.now()))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Lista de itens deve ser nao-nula");
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime time = LocalDateTime.now();

        var pedido1 = Pedido.novoPedido(new IdCliente(1), null,
                List.of(new ItemPedido(1, 2), new ItemPedido(3, 4)),
                "Test Observations", FORMA_PAGAMENTO_DINHEIRO, time);

        var pedido2 = Pedido.novoPedido(new IdCliente(1), null,
                List.of(new ItemPedido(1, 2), new ItemPedido(3, 4)),
                "Test Observations", FORMA_PAGAMENTO_DINHEIRO, time);

        var pedido3 = Pedido.novoPedido(null, "Cliente Test",
                List.of(new ItemPedido(1, 2), new ItemPedido(3, 4)),
                "Test Observations", FORMA_PAGAMENTO_DINHEIRO, time);

        assertThat(pedido1).isEqualTo(pedido2);
        assertThat(pedido1.hashCode()).isEqualTo(pedido2.hashCode());
        assertThat(pedido1).isNotEqualTo(pedido3);
        assertThat(pedido1.hashCode()).isNotEqualTo(pedido3.hashCode());

        System.out.println(pedido1);
    }

    private static final IdFormaPagamento FORMA_PAGAMENTO_DINHEIRO = new IdFormaPagamento("DINHEIRO");
}