package com.example.fiap.archburgers.domain.external;

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

}
