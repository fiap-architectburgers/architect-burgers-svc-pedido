package com.example.fiap.archburgers.domain.valueobjects;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record Cpf(String cpfNum) {

    /**
     * @throws IllegalArgumentException quando o CPF é inválido
     */
    public Cpf {
        if (cpfNum == null)
            throw new IllegalArgumentException("CPF nao pode ser nulo");

        if (!isCpfValid(cpfNum))
            throw new IllegalArgumentException("CPF invalido");

    }

    private boolean isCpfValid(@NotNull String cpfNum) {
        if (cpfNum.length() != 11)
            return false;

        var sum = 0;

        if (!Character.isDigit(cpfNum.charAt(9)) || !Character.isDigit(cpfNum.charAt(10)))
            return false;

        for (int i = 0; i <= 8; i++) {
            if (!Character.isDigit(cpfNum.charAt(i)))
                return false;

            var digit = Character.digit(cpfNum.charAt(i), 10);
            var multiplier = 10 - i;
            sum += digit * multiplier;
        }

        var remainder1 = sum % 11;
        int verificationDigit1;
        if (remainder1 <= 1)
            verificationDigit1 = 0;
        else
            verificationDigit1 = 11 - remainder1;

        sum = 0;
        for (int i = 0; i <= 9; i++) {
            var digit = Character.digit(cpfNum.charAt(i), 10);
            var multiplier = 11 - i;
            sum += digit * multiplier;
        }

        var remainder2 = sum % 11;
        int verificationDigit2;
        if (remainder2 <= 1)
            verificationDigit2 = 0;
        else
            verificationDigit2 = 11 - remainder2;

        return Character.digit(cpfNum.charAt(9), 10) == verificationDigit1
                && Character.digit(cpfNum.charAt(10), 10) == verificationDigit2;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cpf cpf = (Cpf) o;
        return Objects.equals(cpfNum, cpf.cpfNum);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(cpfNum);
    }

    @Override
    public String toString() {
        return "CPF{" + cpfNum + '}';
    }
}
