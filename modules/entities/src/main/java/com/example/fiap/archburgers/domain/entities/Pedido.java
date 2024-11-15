package com.example.fiap.archburgers.domain.entities;

import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.external.Pagamento;
import com.example.fiap.archburgers.domain.valueobjects.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class Pedido {
    private final @Nullable Integer id;
    private final @Nullable IdCliente idClienteIdentificado;
    private final @Nullable String nomeClienteNaoIdentificado;
    private final @NotNull List<ItemPedido> itens;
    private final @Nullable String observacoes;
    private final @NotNull StatusPedido status;
    private final @NotNull IdFormaPagamento formaPagamento;
    private final @NotNull LocalDateTime dataHoraPedido;

    public static Pedido novoPedido(
            IdCliente idClienteIdentificado,
            String nomeClienteNaoIdentificado,

            List<ItemPedido> itens,
            String observacoes,

            IdFormaPagamento formaPagamento,
            LocalDateTime dataHoraPedido) {

        if (itens == null || itens.isEmpty()) {
            throw new DomainArgumentException("Pedido deve conter itens");
        }

        return new Pedido(null, idClienteIdentificado, nomeClienteNaoIdentificado,
                itens, observacoes, StatusPedido.PAGAMENTO, formaPagamento, dataHoraPedido);
    }

    public static Pedido pedidoRecuperado(
            @Nullable Integer id,

            @Nullable IdCliente idClienteIdentificado,
            @Nullable String nomeClienteNaoIdentificado,

            @Nullable List<ItemPedido> itens,

            @Nullable String observacoes,

            @Nullable StatusPedido status,

            @Nullable IdFormaPagamento formaPagamento,

            @Nullable LocalDateTime dataHoraPedido
    ) {
        if (id == null) {
            throw new DomainArgumentException("Pedido existente deve possuir id");
        }

        return new Pedido(id, idClienteIdentificado, nomeClienteNaoIdentificado, itens, observacoes,
                status, formaPagamento, dataHoraPedido);
    }

    private Pedido(
            @Nullable Integer id,

            @Nullable IdCliente idClienteIdentificado,
            @Nullable String nomeClienteNaoIdentificado,

            @Nullable List<ItemPedido> itens,

            @Nullable String observacoes,

            @Nullable StatusPedido status,

            @Nullable IdFormaPagamento formaPagamento,

            @Nullable LocalDateTime dataHoraPedido
    ) {
        if (idClienteIdentificado != null && nomeClienteNaoIdentificado != null) {
            throw new DomainArgumentException("Pedido nao pode ter ambos idClienteIdentificado e nomeClienteNaoIdentificado");
        }
        if (idClienteIdentificado == null && nomeClienteNaoIdentificado == null) {
            throw new DomainArgumentException("Pedido deve ter idClienteIdentificado ou nomeClienteNaoIdentificado");
        }
        if (status == null) {
            throw new DomainArgumentException("Pedido deve conter status");
        }
        if (formaPagamento == null) {
            throw new DomainArgumentException("Pedido deve conter formaPagamento");
        }
        if (dataHoraPedido == null) {
            throw new DomainArgumentException("Pedido deve conter dataHoraPedido");
        }
        if (itens == null) {
            throw new DomainArgumentException("Lista de itens deve ser nao-nula");
        }

        this.id = id;
        this.idClienteIdentificado = idClienteIdentificado;
        this.nomeClienteNaoIdentificado = nomeClienteNaoIdentificado;
        this.itens = itens;
        this.observacoes = observacoes;
        this.status = status;
        this.formaPagamento = formaPagamento;
        this.dataHoraPedido = dataHoraPedido;
    }

    public Pedido confirmarPagamento(Pagamento pagamento) {
        if (pagamento == null) {
            throw new DomainArgumentException("Pagamento nulo");
        }
        if (pagamento.idPagamento() == null) {
            throw new DomainArgumentException("Pagamento deve estar gravado");
        }
        if (status != StatusPedido.PAGAMENTO) {
            throw new DomainArgumentException("Status invalido para pagamento: " + status);
        }
        if (pagamento.status() != StatusPagamento.FINALIZADO) {
            throw new DomainArgumentException("Pagamento nao esta finalizado");
        }

        return new Pedido(id, idClienteIdentificado, nomeClienteNaoIdentificado,
                itens, observacoes, StatusPedido.RECEBIDO, formaPagamento, dataHoraPedido);
    }

    public Pedido validar() {
        if (status != StatusPedido.RECEBIDO) {
            throw new DomainArgumentException("Status invalido para validação do pedido: " + status);
        }
        return new Pedido(id, idClienteIdentificado, nomeClienteNaoIdentificado,
                itens, observacoes, StatusPedido.PREPARACAO, formaPagamento, dataHoraPedido);
    }

    public Pedido setPronto() {
        if (status != StatusPedido.PREPARACAO) {
            throw new DomainArgumentException("Status invalido para mudar para Pronto: " + status);
        }
        return new Pedido(id, idClienteIdentificado, nomeClienteNaoIdentificado,
                itens, observacoes, StatusPedido.PRONTO, formaPagamento, dataHoraPedido);
    }

    public Pedido finalizar() {
        if (status != StatusPedido.PRONTO) {
            throw new DomainArgumentException("Status invalido para finalizar: " + status);
        }
        return new Pedido(id, idClienteIdentificado, nomeClienteNaoIdentificado,
                itens, observacoes, StatusPedido.FINALIZADO, formaPagamento, dataHoraPedido);
    }

    public Pedido cancelar() {
        return new Pedido(id, idClienteIdentificado, nomeClienteNaoIdentificado,
                itens, observacoes, StatusPedido.CANCELADO, formaPagamento, dataHoraPedido);
    }

    public Pedido withId(int newId) {
        return new Pedido(newId, idClienteIdentificado, nomeClienteNaoIdentificado, itens, observacoes, status, formaPagamento, dataHoraPedido);
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

    public @NotNull StatusPedido status() {
        return status;
    }

    public @NotNull IdFormaPagamento formaPagamento() {
        return formaPagamento;
    }

    public @NotNull LocalDateTime dataHoraPedido() {
        return dataHoraPedido;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Pedido) obj;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.idClienteIdentificado, that.idClienteIdentificado) &&
                Objects.equals(this.nomeClienteNaoIdentificado, that.nomeClienteNaoIdentificado) &&
                Objects.equals(this.itens, that.itens) &&
                Objects.equals(this.observacoes, that.observacoes) &&
                Objects.equals(this.status, that.status) &&
                Objects.equals(this.formaPagamento, that.formaPagamento) &&
                Objects.equals(this.dataHoraPedido, that.dataHoraPedido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idClienteIdentificado, nomeClienteNaoIdentificado, itens, observacoes, status, formaPagamento, dataHoraPedido);
    }

    @Override
    public String toString() {
        return "Pedido[" +
                "id=" + id + ", " +
                "idClienteIdentificado=" + idClienteIdentificado + ", " +
                "nomeClienteNaoIdentificado=" + nomeClienteNaoIdentificado + ", " +
                "itens=" + itens + ", " +
                "observacoes=" + observacoes + ", " +
                "status=" + status + ", " +
                "formaPagamento=" + formaPagamento + ", " +
                "dataHoraPedido=" + dataHoraPedido + ']';
    }

}
