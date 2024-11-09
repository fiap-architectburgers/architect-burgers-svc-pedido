package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.entities.Pedido;
import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;

/**
 * Representa comunicação com o microsserviço de Pagamentos
 */
public interface PagamentoService {
    IdFormaPagamento validarFormaPagamento(String idFormaPagamento);

    void iniciarPagamento(Pedido pedido);
}
