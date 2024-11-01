package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.entities.ItemCardapio;
import com.example.fiap.archburgers.domain.entities.ItemPedido;

import java.util.List;

/**
 * Representa comunicação com o microsserviço Catálogo de Produtos (cardápio)
 */
public interface CatalogoProdutosService {
    ItemCardapio findAll();
}
