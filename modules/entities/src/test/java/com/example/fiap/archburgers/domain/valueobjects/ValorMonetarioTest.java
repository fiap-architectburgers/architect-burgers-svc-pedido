package com.example.fiap.archburgers.domain.valueobjects;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;


class ValorMonetarioTest {

    @Test
    void adjustScaleIfSmaller() {
        assertThat(new ValorMonetario("25.98")).isEqualTo(new ValorMonetario("25.98"));

        assertThat(new ValorMonetario("123456.9")).isEqualTo(new ValorMonetario("123456.90"));
        assertThat(new ValorMonetario("13")).isEqualTo(new ValorMonetario("13.00"));
    }

    @Test
    void rejectInvalidValues() {
        var e = assertThrows(IllegalArgumentException.class, () -> new ValorMonetario((BigDecimal) null));
        assertThat(e).hasMessage("Valor nao pode ser nulo");

        e = assertThrows(IllegalArgumentException.class, () -> new ValorMonetario("-1"));
        assertThat(e).hasMessage("Valor nao pode ser negativo");

        e = assertThrows(IllegalArgumentException.class, () -> new ValorMonetario("25.999"));
        assertThat(e).hasMessage("Valor monetario invalido, mais que 2 digitos decimais: 25.999");

        e = assertThrows(IllegalArgumentException.class, () -> new ValorMonetario("15.0003"));
        assertThat(e).hasMessage("Valor monetario invalido, mais que 2 digitos decimais: 15.0003");
    }

    @Test
    void somar() {
        assertThat(new ValorMonetario(".1")
                .somar(new ValorMonetario("0"))).isEqualTo(new ValorMonetario("0.10"));

        assertThat(new ValorMonetario("25.98")
                .somar(new ValorMonetario("10.5"))).isEqualTo(new ValorMonetario("36.48"));
    }

    @Test
    void asBigDecimal() {
        assertThat(new ValorMonetario("25.98").asBigDecimal()).isEqualTo(new BigDecimal("25.98"));
        assertThat(new ValorMonetario("123456.90").asBigDecimal()).isEqualTo(new BigDecimal("123456.90"));
        assertThat(new ValorMonetario("13.00").asBigDecimal()).isEqualTo(new BigDecimal("13.00"));
    }

    @Test
    void testEqualsAndHashCode() {
        ValorMonetario valor1 = new ValorMonetario("25.98");
        ValorMonetario valor2 = new ValorMonetario("25.98");

        assertThat(valor1).isEqualTo(valor2);
        assertThat(valor1.hashCode()).isEqualTo(valor2.hashCode());
    }

    @Test
    void testToString() {
        assertThat(new ValorMonetario("25.98").toString()).isEqualTo("R$25.98");
    }
}