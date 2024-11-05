package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.valueobjects.TipoItemCardapio;
import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record ItemCardapio(@NotNull Integer id,
                           @NotNull TipoItemCardapio tipo,
                           @NotNull String nome,
                           @NotNull String descricao,
                           @NotNull ValorMonetario valor) {

    public ItemCardapio withId(Integer newId) {
        return new ItemCardapio(newId, tipo, nome, descricao, valor);
    }

    public static ValorMonetario somarValores(List<ItemCardapio> itens) {
        if (itens == null || itens.isEmpty()) {
            return ValorMonetario.ZERO;
        }

        var soma = ValorMonetario.ZERO;
        for (ItemCardapio item : itens) {
            soma = soma.somar(item.valor);
        }
        return soma;
    }
}
