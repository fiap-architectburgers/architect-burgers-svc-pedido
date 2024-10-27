package com.example.fiap.archburgers.domain.valueobjects;

public enum TipoItemCardapio {
    LANCHE("L"),
    ACOMPANHAMENTO("A"),
    BEBIDA("B"),
    SOBREMESA("S");

    private final String abreviacao;

    TipoItemCardapio(String abreviacao) {
        this.abreviacao = abreviacao;
    }

    public String getAbreviacao() {
        return abreviacao;
    }

    public static TipoItemCardapio getByAbreviacao(String abreviacao) {
        return switch (abreviacao) {
            case "L" -> LANCHE;
            case "A" -> ACOMPANHAMENTO;
            case "B" -> BEBIDA;
            case "S" -> SOBREMESA;
            default -> throw new IllegalArgumentException("Invalid TipoItemCardapio: " + abreviacao);
        };
    }
}
