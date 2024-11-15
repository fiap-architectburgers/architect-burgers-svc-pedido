package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.entities.Pedido;
import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import com.example.fiap.archburgers.domain.valueobjects.PedidoDetalhe;

import java.util.List;
import java.util.function.Consumer;

/**
 * Representa comunicação com o microsserviço de Pagamentos
 */
public interface PagamentoService {
    IdFormaPagamento validarFormaPagamento(String idFormaPagamento) throws Exception;

    void iniciarPagamento(PedidoDetalhe pedidoDetalhe) throws Exception;

    /**
     * Recebe as mensagens disponíveis na fila de confirmação de pagamento. Se houver mensagens o
     * callback é invocado com cada objeto de Pagamento recebido.
     * Quando a chamada do callback completa sem exceções a mensagem é removida da fila.
     */
    void receberConfirmacoes(Consumer<Pagamento> callback) throws Exception;
}
