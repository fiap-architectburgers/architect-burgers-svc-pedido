package com.example.fiap.archburgers.domain.datasource;

import com.example.fiap.archburgers.domain.external.ItemCardapio;
import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.valueobjects.TipoItemCardapio;

import java.util.List;

public interface ItemCardapioDataSource {
    ItemCardapio findById(int id);
    List<ItemCardapio> findAll();

    List<ItemPedido> findByCarrinho(int idCarrinho);
    List<ItemPedido> findByPedido(int idPedido);

    List<ItemCardapio> findByTipo(TipoItemCardapio filtroTipo);

    ItemCardapio salvarNovo(ItemCardapio itemCardapio);

    void atualizar(ItemCardapio itemCardapio);

    void excluir(int idItemCardapio);
}
