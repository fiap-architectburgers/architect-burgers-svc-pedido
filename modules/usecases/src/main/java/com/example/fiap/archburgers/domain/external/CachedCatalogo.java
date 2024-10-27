package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.entities.ItemCardapio;
import com.example.fiap.archburgers.domain.entities.ItemPedido;

import java.util.List;
import java.util.Map;

public class CachedCatalogo {
    public CachedCatalogo(CatalogoProdutosService catalogoProdutosService) {

    }

    public Map<Integer, ItemCardapio> findAll(List<ItemPedido> itensIds) {
        return null;
    }

    public ItemCardapio findById(int idItemCardapio) {
        return null;
    }
}
