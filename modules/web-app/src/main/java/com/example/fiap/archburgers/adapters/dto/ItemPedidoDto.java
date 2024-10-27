package com.example.fiap.archburgers.adapters.dto;

public record ItemPedidoDto(
        int numSequencia,
        Integer idItemCardapio,
        String tipo,
        String nome,
        String descricao,
        ValorMonetarioDto valor
) {

}
