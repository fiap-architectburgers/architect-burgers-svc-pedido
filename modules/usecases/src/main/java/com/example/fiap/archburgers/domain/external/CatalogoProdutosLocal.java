package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Mantém uma versão local dos dados de catálogo de produtos (cardápio), consultando serviços externos
 * conforme necessário de forma transparente para quem utiliza as funções
 */
public class CatalogoProdutosLocal {
    private static final long DEFAULT_CACHE_EXPIRATION_MS = 2 * 60 * 1000;  // cache expires after 2 minutes

    private final CatalogoProdutosService catalogoProdutosService;

    private final long cacheExpiration;
    private final ConcurrentMap<Integer, ItemCardapio> cache = new ConcurrentHashMap<>();

    private volatile long lastCacheUsage;

    public CatalogoProdutosLocal(CatalogoProdutosService catalogoProdutosService) {
        this.catalogoProdutosService = catalogoProdutosService;
        this.cacheExpiration = DEFAULT_CACHE_EXPIRATION_MS;
    }

    @VisibleForTesting
    CatalogoProdutosLocal(CatalogoProdutosService catalogoProdutosService, long cacheExpiration) {
        this.catalogoProdutosService = catalogoProdutosService;
        this.cacheExpiration = cacheExpiration;
    }

    public Map<Integer, ItemCardapio> findAll(Collection<ItemPedido> itensIds) {
        Map<Integer, ItemCardapio> allItens = allItensAsMap();

        return itensIds.stream()
                .map(ItemPedido::idItemCardapio)
                .distinct()
                .map(idItemCardapio -> {
                    var item = allItens.get(idItemCardapio);
                    if (item == null) {
                        throw new DomainArgumentException("Lista de itens inválida. Item ["
                                + idItemCardapio + "] não encontrado");
                    }
                    return item;
                })
                .collect(Collectors.toMap(
                        ItemCardapio::id, Function.identity()
                ));
    }

    public ItemCardapio findById(int idItemCardapio) {
        return allItensAsMap().get(idItemCardapio);
    }

    private Map<Integer, ItemCardapio> allItensAsMap() {
        long currentTime = System.currentTimeMillis();

        // If cache is empty or expired, update the cache
        if ((currentTime - lastCacheUsage) > cacheExpiration) {
            synchronized (this) {
                if ((currentTime - lastCacheUsage) > cacheExpiration) {
                    var updated = catalogoProdutosService.findAll()
                            .stream().collect(Collectors.toMap(
                                    ItemCardapio::id,
                                    Function.identity()
                            ));

                    cache.clear();
                    cache.putAll(updated);

                    lastCacheUsage = currentTime;
                }
            }
        }

        return cache;
    }
}
