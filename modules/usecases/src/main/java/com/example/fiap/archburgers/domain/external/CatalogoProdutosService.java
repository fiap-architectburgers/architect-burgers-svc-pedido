package com.example.fiap.archburgers.domain.external;

import java.util.Collection;

/**
 * Representa comunicação com o microsserviço Catálogo de Produtos (cardápio)
 */
public interface CatalogoProdutosService {
    Collection<ItemCardapio> findAll();
}
