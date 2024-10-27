package com.example.fiap.archburgers.domain.entities;

import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import com.example.fiap.archburgers.domain.valueobjects.StatusPagamento;
import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public record Pagamento(
        @Nullable Integer id,
        @NotNull Integer idPedido,
        @NotNull IdFormaPagamento formaPagamento,
        @NotNull StatusPagamento status,
        @NotNull ValorMonetario valor,
        @NotNull LocalDateTime dataHoraCriacao,
        @NotNull LocalDateTime dataHoraAtualizacao,
        @Nullable String codigoPagamentoCliente,
        @Nullable String idPedidoSistemaExterno
) {

    public static Pagamento registroInicial(@NotNull Integer idPedido,
                                            @NotNull IdFormaPagamento formaPagamento,
                                            @NotNull ValorMonetario valor,
                                            @NotNull LocalDateTime dataHora,
                                            @Nullable String codigoPagamentoCliente,
                                            @Nullable String idPedidoSistemaExterno) {
        return new Pagamento(null, idPedido, formaPagamento, StatusPagamento.PENDENTE,
                valor, dataHora, dataHora,
                codigoPagamentoCliente, idPedidoSistemaExterno);
    }

    public Pagamento finalizar(@NotNull LocalDateTime dataHora) {
        return new Pagamento(id, idPedido, formaPagamento, StatusPagamento.FINALIZADO,
                valor, dataHoraCriacao, dataHora, codigoPagamentoCliente, idPedidoSistemaExterno);
    }

    public Pagamento finalizar(@NotNull LocalDateTime dataHora, @NotNull String newIdSistemaExterno) {
        return new Pagamento(id, idPedido, formaPagamento, StatusPagamento.FINALIZADO,
                valor, dataHoraCriacao, dataHora, codigoPagamentoCliente, newIdSistemaExterno);
    }

    public Pagamento withId(int newId) {
        return new Pagamento(newId, idPedido, formaPagamento, status, valor, dataHoraCriacao, dataHoraAtualizacao,
                codigoPagamentoCliente, idPedidoSistemaExterno);
    }
}
