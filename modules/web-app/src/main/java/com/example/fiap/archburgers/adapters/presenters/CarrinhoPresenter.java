package com.example.fiap.archburgers.adapters.presenters;

import com.example.fiap.archburgers.adapters.dto.CarrinhoDto;
import com.example.fiap.archburgers.adapters.dto.ItemPedidoDto;
import com.example.fiap.archburgers.adapters.dto.ValorMonetarioDto;
import com.example.fiap.archburgers.domain.entities.Carrinho;
import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.utils.DateUtils;
import com.example.fiap.archburgers.domain.valueobjects.CarrinhoDetalhe;

import java.util.ArrayList;
import java.util.List;

public class CarrinhoPresenter {

    public static CarrinhoDto entityToPresentationDto(CarrinhoDetalhe carrinhoDetalhe) {
        List<ItemPedidoDto> dtoItens = new ArrayList<>();

        for (ItemPedido itemPedido : carrinhoDetalhe.carrinho().itens()) {
            var itemDetalhe = carrinhoDetalhe.detalhesItens().get(itemPedido.idItemCardapio());
            if (itemDetalhe == null)
                throw new IllegalStateException("Inconsistent data: Item id=[" + itemPedido.idItemCardapio() + "] details missing");

            dtoItens.add(new ItemPedidoDto(itemPedido.numSequencia(), itemPedido.idItemCardapio(),
                    itemDetalhe.tipo().name(),
                    itemDetalhe.nome(), itemDetalhe.descricao(),
                    ValorMonetarioDto.from(itemDetalhe.valor())));
        }

        return new CarrinhoDto(carrinhoDetalhe.carrinho().id(),
                carrinhoDetalhe.carrinho().idClienteIdentificado() != null ? carrinhoDetalhe.carrinho().idClienteIdentificado().id() : null,
                carrinhoDetalhe.carrinho().nomeClienteNaoIdentificado(),
                dtoItens, carrinhoDetalhe.carrinho().observacoes(),
                ValorMonetarioDto.from(carrinhoDetalhe.getValorTotal()),
                DateUtils.toTimestamp(carrinhoDetalhe.carrinho().dataHoraCarrinhoCriado()));
    }
}
