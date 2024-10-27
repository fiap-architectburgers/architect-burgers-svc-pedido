package com.example.fiap.archburgers.domain.valueobjects;

import com.example.fiap.archburgers.domain.entities.Carrinho;
import com.example.fiap.archburgers.domain.entities.ItemCardapio;

import java.util.Map;

public record CarrinhoDetalhe(Carrinho carrinho, Map<Integer, ItemCardapio> detalhesItens) {

    public ValorMonetario getValorTotal() {
        return ItemCardapio.somarValores(detalhesItens.values().stream().toList());
    }
}
