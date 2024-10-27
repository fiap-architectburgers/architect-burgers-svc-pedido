package com.example.fiap.archburgers.domain.entities;

import com.example.fiap.archburgers.domain.valueobjects.IdCliente;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class Carrinho {
    private final @Nullable Integer id;
    private final @Nullable IdCliente idClienteIdentificado;
    private final @Nullable String nomeClienteNaoIdentificado;
    private final @NotNull List<ItemPedido> itens;
    private final @Nullable String observacoes;
    private final @NotNull LocalDateTime dataHoraCarrinhoCriado;

    public static Carrinho carrinhoSalvoClienteIdentificado(@NotNull Integer id,
                                                            @NotNull IdCliente idCliente,
                                                            @NotNull List<ItemPedido> itens,
                                                            @Nullable String observacoes,
                                                            @NotNull LocalDateTime dataHoraCarrinhoCriado) {
        return new Carrinho(id, idCliente, null, itens, observacoes, dataHoraCarrinhoCriado);
    }

    public static Carrinho newCarrinhoVazioClienteIdentificado(@NotNull IdCliente idCliente,
                                                               @NotNull LocalDateTime dataHoraCarrinhoCriado) {
        return new Carrinho(null, idCliente, null, List.of(), null, dataHoraCarrinhoCriado);
    }

    public static Carrinho newCarrinhoVazioClienteNaoIdentificado(@NotNull String nomeCliente,
                                                                  @NotNull LocalDateTime dataHoraCarrinhoCriado) {
        return new Carrinho(null, null, nomeCliente, List.of(), null, dataHoraCarrinhoCriado);
    }

    public Carrinho(
            @Nullable Integer id,
            @Nullable IdCliente idClienteIdentificado,
            @Nullable String nomeClienteNaoIdentificado,

            @NotNull List<ItemPedido> itens,

            @Nullable String observacoes,

            @NotNull LocalDateTime dataHoraCarrinhoCriado
    ) {
        this.id = id;
        this.idClienteIdentificado = idClienteIdentificado;
        this.nomeClienteNaoIdentificado = nomeClienteNaoIdentificado;
        this.itens = itens;
        this.observacoes = observacoes;
        this.dataHoraCarrinhoCriado = dataHoraCarrinhoCriado;
    }

    public Carrinho withId(Integer newId) {
        return new Carrinho(newId, idClienteIdentificado, nomeClienteNaoIdentificado, itens, observacoes, dataHoraCarrinhoCriado);
    }

    public Carrinho adicionarItem(ItemCardapio newItem) {
        List<ItemPedido> newList = new ArrayList<>(itens);
        var maxSequencia = itens.stream().mapToInt(ItemPedido::numSequencia).max();
        var nextSequencia = maxSequencia.orElse(0) + 1;
        newList.add(new ItemPedido(nextSequencia, newItem.id()));

        return new Carrinho(id, idClienteIdentificado, nomeClienteNaoIdentificado,
                newList, observacoes, dataHoraCarrinhoCriado);
    }

    public Carrinho setObservacoes(String obs) {
        return new Carrinho(id, idClienteIdentificado, nomeClienteNaoIdentificado,
                itens, obs, dataHoraCarrinhoCriado);
    }

    public @Nullable Integer id() {
        return id;
    }

    public @Nullable IdCliente idClienteIdentificado() {
        return idClienteIdentificado;
    }

    public @Nullable String nomeClienteNaoIdentificado() {
        return nomeClienteNaoIdentificado;
    }

    public @NotNull List<ItemPedido> itens() {
        return itens;
    }

    public @Nullable String observacoes() {
        return observacoes;
    }

    public @NotNull LocalDateTime dataHoraCarrinhoCriado() {
        return dataHoraCarrinhoCriado;
    }

    public Carrinho deleteItem(int numSequencia) {
        List<ItemPedido> newItens = new ArrayList<>();

        boolean found = false;
        int seq = 0;
        for (ItemPedido item : itens) {
            if (item.numSequencia() == numSequencia) {
                found = true;
            } else {
                newItens.add(new ItemPedido(++seq, item.idItemCardapio()));
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Não é possível excluir item fora da lista: " + numSequencia);
        }

        return new Carrinho(id, idClienteIdentificado, nomeClienteNaoIdentificado,
                newItens, observacoes, dataHoraCarrinhoCriado);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Carrinho) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.idClienteIdentificado, that.idClienteIdentificado) &&
                Objects.equals(this.nomeClienteNaoIdentificado, that.nomeClienteNaoIdentificado) &&
                Objects.equals(this.itens, that.itens) &&
                Objects.equals(this.observacoes, that.observacoes) &&
                Objects.equals(this.dataHoraCarrinhoCriado, that.dataHoraCarrinhoCriado);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idClienteIdentificado, nomeClienteNaoIdentificado, itens, observacoes, dataHoraCarrinhoCriado);
    }

    @Override
    public String toString() {
        return "Carrinho[" +
                "id=" + id + ", " +
                "idClienteIdentificado=" + idClienteIdentificado + ", " +
                "nomeClienteNaoIdentificado=" + nomeClienteNaoIdentificado + ", " +
                "itens=" + itens + ", " +
                "observacoes=" + observacoes + ", " +
                "dataHoraCarrinhoCriado=" + dataHoraCarrinhoCriado + ']';
    }
}
