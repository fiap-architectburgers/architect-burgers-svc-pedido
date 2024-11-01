package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.entities.ItemCardapio;
import com.example.fiap.archburgers.domain.entities.ItemPedido;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Mantém uma versão local dos dados de catálogo de produtos (cardápio), consultando serviços externos
 * conforme necessário de forma transparente para quem utiliza as funções
 */
public class CatalogoProdutosLocal {
    public CatalogoProdutosLocal(CatalogoProdutosService catalogoProdutosService) {

    }

    public Map<Integer, ItemCardapio> findAll(Collection<ItemPedido> itensIds) {
        return null;
    }

    public ItemCardapio findById(int idItemCardapio) {
        return null;
    }
}
