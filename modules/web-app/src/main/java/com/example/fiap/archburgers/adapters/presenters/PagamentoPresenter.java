package com.example.fiap.archburgers.adapters.presenters;

import com.example.fiap.archburgers.adapters.dto.PagamentoDto;
import com.example.fiap.archburgers.adapters.dto.ValorMonetarioDto;
import com.example.fiap.archburgers.domain.external.Pagamento;
import com.example.fiap.archburgers.domain.utils.DateUtils;

public class PagamentoPresenter {

    public static PagamentoDto entityToPresentationDto(Pagamento pagamento) {
        return new PagamentoDto(
                pagamento.id(),
                pagamento.idPedido(),
                pagamento.formaPagamento().codigo(),
                pagamento.status().name(),
                ValorMonetarioDto.from(pagamento.valor()),
                DateUtils.toTimestamp(pagamento.dataHoraCriacao()),
                DateUtils.toTimestamp(pagamento.dataHoraAtualizacao()),
                pagamento.codigoPagamentoCliente(),
                pagamento.idPedidoSistemaExterno()
        );
    }
}
