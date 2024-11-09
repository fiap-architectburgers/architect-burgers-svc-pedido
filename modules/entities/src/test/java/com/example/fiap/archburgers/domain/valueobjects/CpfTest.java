package com.example.fiap.archburgers.domain.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CpfTest {

    @Test
    void validate() {
        // Brancos, nulos, tamanhos incorretos, digitos invalidos...
        assertThrows(IllegalArgumentException.class, () -> new Cpf(null));
        assertThrows(IllegalArgumentException.class, () -> new Cpf(""));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("1233211234"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("123321123401"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("A1233211234"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("5123321123A"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("512332112A4"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("1233211234z"));

        // DV invalido
        assertThrows(IllegalArgumentException.class, () -> new Cpf("12332112339"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("12332112341"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("12332112330"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("12332112350"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("99988877713"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("99988877715"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("99988877704"));
        assertThrows(IllegalArgumentException.class, () -> new Cpf("99988877794"));

        // Valido
        assertThat(new Cpf("12332112340").cpfNum()).isEqualTo("12332112340");
        assertThat(new Cpf("99988877714").cpfNum()).isEqualTo("99988877714");
    }

    @Test
    void testEqualsAndHashCode() {
        Cpf cpf1 = new Cpf("12332112340");
        Cpf cpf2 = new Cpf("12332112340");
        Cpf cpf3 = new Cpf("99988877714");

        assertThat(cpf1.equals(cpf1)).isTrue();
        assertThat(cpf1).isEqualTo(cpf2);
        assertThat(cpf1.hashCode()).isEqualTo(cpf2.hashCode());
        assertThat(cpf1).isNotEqualTo(cpf3);
        assertThat(cpf2).isNotEqualTo(cpf3);
    }

    @Test
    void testToString() {
        Cpf cpf = new Cpf("12332112340");
        assertThat(cpf.toString()).isEqualTo("CPF{12332112340}");
    }
}