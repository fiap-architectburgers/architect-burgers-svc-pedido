package com.example.fiap.archburgers.adapters.presenters;

import com.example.fiap.archburgers.adapters.dto.ItemPedidoDto;
import com.example.fiap.archburgers.adapters.dto.PedidoDto;
import com.example.fiap.archburgers.adapters.dto.ValorMonetarioDto;
import com.example.fiap.archburgers.domain.external.ItemCardapio;
import com.example.fiap.archburgers.domain.utils.DateUtils;
import com.example.fiap.archburgers.domain.valueobjects.PedidoDetalhe;

import java.util.List;

public class PedidoPresenter {
    public static PedidoDto entityToPresentationDto(PedidoDetalhe pedidoDetalhe) {
        List<ItemPedidoDto> dtoItens = pedidoDetalhe.pedido().itens().stream().map(itemPedido -> {
                    ItemCardapio itemDetalhe = pedidoDetalhe.detalhesItens().get(itemPedido.idItemCardapio());

                    return new ItemPedidoDto(itemPedido.numSequencia(),
                            itemPedido.idItemCardapio(), itemDetalhe.tipo().name(),
                            itemDetalhe.nome(), itemDetalhe.descricao(),
                            ValorMonetarioDto.from(itemDetalhe.valor()));
                })
                .toList();

        return new PedidoDto(pedidoDetalhe.pedido().id(),
                pedidoDetalhe.pedido().idClienteIdentificado() != null ? pedidoDetalhe.pedido().idClienteIdentificado().id() : null,
                pedidoDetalhe.pedido().nomeClienteNaoIdentificado(),
                dtoItens,
                pedidoDetalhe.pedido().observacoes(),
                pedidoDetalhe.pedido().status().name(),
                pedidoDetalhe.pedido().formaPagamento().codigo(),
                ValorMonetarioDto.from(pedidoDetalhe.getValorTotal()),
                DateUtils.toTimestamp(pedidoDetalhe.pedido().dataHoraPedido()));
    }
}
