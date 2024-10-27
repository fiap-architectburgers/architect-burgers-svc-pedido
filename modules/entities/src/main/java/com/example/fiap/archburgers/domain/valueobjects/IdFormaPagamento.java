package com.example.fiap.archburgers.domain.valueobjects;

import org.jetbrains.annotations.NotNull;

public record IdFormaPagamento(@NotNull String codigo) {

    public static IdFormaPagamento DINHEIRO = new IdFormaPagamento("DINHEIRO");
    public static IdFormaPagamento CARTAO_MAQUINA = new IdFormaPagamento("CARTAO_MAQUINA");
}
