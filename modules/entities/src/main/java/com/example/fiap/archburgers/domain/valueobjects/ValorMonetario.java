package com.example.fiap.archburgers.domain.valueobjects;

import org.jetbrains.annotations.VisibleForTesting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public class ValorMonetario {
    public static final ValorMonetario ZERO = new ValorMonetario(BigDecimal.ZERO);

    private final BigDecimal valor;

    public ValorMonetario(BigDecimal valor) {
        if (valor == null)
            throw new IllegalArgumentException("Valor nao pode ser nulo");

        if (valor.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Valor nao pode ser negativo");

        if (valor.scale() > 2)
            throw new IllegalArgumentException("Valor monetario invalido, mais que 2 digitos decimais: " + valor);

        this.valor = valor.setScale(2, RoundingMode.HALF_DOWN);
    }

    @VisibleForTesting
    public ValorMonetario(String valorStr) {
        this(new BigDecimal(valorStr));
    }

    public BigDecimal asBigDecimal() {
        return valor;
    }

    public ValorMonetario somar(ValorMonetario outro) {
        return new ValorMonetario(valor.add(outro.valor));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValorMonetario that = (ValorMonetario) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(valor);
    }

    @Override
    public String toString() {
        return "R$" + valor;
    }
}
