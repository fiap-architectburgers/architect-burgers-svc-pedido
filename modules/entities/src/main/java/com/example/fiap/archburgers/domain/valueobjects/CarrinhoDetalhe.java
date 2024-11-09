package com.example.fiap.archburgers.domain.valueobjects;

import com.example.fiap.archburgers.domain.entities.Carrinho;
import com.example.fiap.archburgers.domain.external.ItemCardapio;

import java.util.List;
import java.util.Map;

public record CarrinhoDetalhe(Carrinho carrinho, Map<Integer, ItemCardapio> detalhesItens) {

    public ValorMonetario getValorTotal() {
        List<ItemCardapio> usedItens = carrinho.itens()
                .stream().map(itemCarrinho -> {
                    ItemCardapio detalheItem = detalhesItens.get(itemCarrinho.idItemCardapio());
                    if (detalheItem == null)
                        throw new IllegalStateException("Dados de Carrinho inconsistentes. Missing " + itemCarrinho.idItemCardapio());
                    return detalheItem;
                })
                .toList();

        return ItemCardapio.somarValores(usedItens);
    }
}
