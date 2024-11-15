package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import com.example.fiap.archburgers.domain.valueobjects.StatusPagamento;
import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;

public record Pagamento(
        String idPagamento,
        Integer idPedido,
        IdFormaPagamento formaPagamento,
        StatusPagamento status,
        ValorMonetario valor,
        LocalDateTime dataHoraCriacao,
        LocalDateTime dataHoraAtualizacao,
        String codigoPagamentoCliente,
        String idPedidoSistemaExterno
) {

}
