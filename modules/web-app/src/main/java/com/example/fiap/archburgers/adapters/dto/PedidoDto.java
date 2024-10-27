package com.example.fiap.archburgers.adapters.dto;

import java.util.List;

public record PedidoDto(
        Integer id,
        Integer idClienteIdentificado,
        String nomeClienteNaoIdentificado,
        List<ItemPedidoDto> itens,
        String observacoes,
        String status,
        String formaPagamento,
        ValorMonetarioDto valorTotal,
        Long dataHoraCarrinhoCriado
) {

}
