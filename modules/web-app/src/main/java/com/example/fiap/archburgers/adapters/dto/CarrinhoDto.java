package com.example.fiap.archburgers.adapters.dto;

import java.util.List;

public record CarrinhoDto(
        Integer id,
        Integer idClienteIdentificado,
        String nomeClienteNaoIdentificado,
        List<ItemPedidoDto> itens,
        String observacoes,
        ValorMonetarioDto valorTotal,
        Long dataHoraCarrinhoCriado
) {


}
