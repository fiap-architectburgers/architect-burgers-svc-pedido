package com.example.fiap.archburgers.adapters.dto;

import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import org.jetbrains.annotations.NotNull;

import java.text.NumberFormat;

public record ValorMonetarioDto(
        String raw,
        String formatted
) {
    public static ValorMonetarioDto from(@NotNull ValorMonetario valor) {
        var nf = NumberFormat.getCurrencyInstance();
        nf.setGroupingUsed(false);
        // Original impl separates with a NBSP, change to simple space
        String formatted = nf.format(valor.asBigDecimal()).replace((char) 0x00A0, ' ');

        return new ValorMonetarioDto(valor.asBigDecimal().toPlainString(),
                formatted);
    }
}
