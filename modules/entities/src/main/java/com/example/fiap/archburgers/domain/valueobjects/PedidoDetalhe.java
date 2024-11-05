package com.example.fiap.archburgers.domain.valueobjects;

import com.example.fiap.archburgers.domain.external.ItemCardapio;
import com.example.fiap.archburgers.domain.entities.Pedido;

import java.util.Map;

public record PedidoDetalhe(Pedido pedido, Map<Integer, ItemCardapio> detalhesItens) {
    public ValorMonetario getValorTotal() {
        return ItemCardapio.somarValores(detalhesItens.values().stream().toList());
    }
}
