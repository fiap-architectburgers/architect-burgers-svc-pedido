package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.valueobjects.TipoItemCardapio;
import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CatalogoProdutosLocal class.
 */
public class CatalogoProdutosLocalTest {
    private CatalogoProdutosService catalogoProdutosService;
    private CatalogoProdutosLocal catalogoProdutosLocal;

    @BeforeEach
    public void setUp() {
        catalogoProdutosService = mock(CatalogoProdutosService.class);
        when(catalogoProdutosService.findAll()).thenReturn(List.of(sampleItem1000, sampleItem1001));

        catalogoProdutosLocal = new CatalogoProdutosLocal(catalogoProdutosService);
    }

    @Test
    public void findById() {
        ItemCardapio result = catalogoProdutosLocal.findById(1000);
        assertThat(result).isSameAs(sampleItem1000);
    }

    @Test
    public void findById_notFound() {
        ItemCardapio result = catalogoProdutosLocal.findById(1099);
        assertThat(result).isNull();
    }

    @Test
    public void findAll() {
        Map<Integer, ItemCardapio> items = catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1001),
                new ItemPedido(2, 1000)
        ));

        assertThat(items).containsExactlyInAnyOrderEntriesOf(Map.of(
                1000, sampleItem1000,
                1001, sampleItem1001
        ));
    }

    @Test
    public void findAll_subsets() {
        Map<Integer, ItemCardapio> items1 = catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1000)
        ));
        assertThat(items1).containsExactlyInAnyOrderEntriesOf(Map.of(
                1000, sampleItem1000
        ));

        Map<Integer, ItemCardapio> items2 = catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1001)
        ));
        assertThat(items2).containsExactlyInAnyOrderEntriesOf(Map.of(
                1001, sampleItem1001
        ));
    }

    @Test
    public void findAll_repeatedItens() {
        Map<Integer, ItemCardapio> items = catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1000),
                new ItemPedido(2, 1000),
                new ItemPedido(3, 1000)
        ));

        assertThat(items).containsExactlyInAnyOrderEntriesOf(Map.of(
                1000, sampleItem1000
        ));
    }

    @Test
    public void findAll_requestHasInvalidItem() {
        assertThatThrownBy(() -> catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1001),
                new ItemPedido(2, 1099)
        )))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessageContaining("Lista de itens inválida. Item [1099] não encontrado");
    }

    @Test
    public void find_useCache() throws Exception {
        catalogoProdutosLocal = new CatalogoProdutosLocal(catalogoProdutosService, 400);

        verifyNoInteractions(catalogoProdutosService);

        catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1000)
        ));

        verify(catalogoProdutosService, times(1)).findAll();

        catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1001)
        ));
        catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1000),
                new ItemPedido(2, 1001)
        ));
        try {
            catalogoProdutosLocal.findAll(List.of(
                    new ItemPedido(1, 999)
            ));
        } catch (Exception ignored) {
        }

        verify(catalogoProdutosService, times(1)).findAll(); // Same cache still in use

        Thread.sleep(500L); // Cache expired during this time

        catalogoProdutosLocal.findAll(List.of(
                new ItemPedido(1, 1001)
        ));

        verify(catalogoProdutosService, times(2)).findAll();
    }

    private final ItemCardapio sampleItem1000 = new ItemCardapio(1000, TipoItemCardapio.LANCHE, "Cheeseburger",
            "Hamburger com queijo", new ValorMonetario("25.90"));
    private final ItemCardapio sampleItem1001 = new ItemCardapio(1001, TipoItemCardapio.LANCHE, "Batata frita M",
            "Batata frita tamanho médio", new ValorMonetario("11.50"));

}