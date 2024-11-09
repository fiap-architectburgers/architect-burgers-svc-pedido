package com.example.fiap.archburgers.domain.valueobjects;

import com.example.fiap.archburgers.domain.external.ItemCardapio;
import com.example.fiap.archburgers.domain.entities.Pedido;

import java.util.List;
import java.util.Map;

public record PedidoDetalhe(Pedido pedido, Map<Integer, ItemCardapio> detalhesItens) {
    public ValorMonetario getValorTotal() {
        List<ItemCardapio> usedItens = pedido.itens()
                .stream().map(itemCarrinho -> {
                    ItemCardapio detalheItem = detalhesItens.get(itemCarrinho.idItemCardapio());
                    if (detalheItem == null)
                        throw new IllegalStateException("Dados de Pedido inconsistentes. Missing " + itemCarrinho.idItemCardapio());
                    return detalheItem;
                })
                .toList();

        return ItemCardapio.somarValores(usedItens);
    }

}
