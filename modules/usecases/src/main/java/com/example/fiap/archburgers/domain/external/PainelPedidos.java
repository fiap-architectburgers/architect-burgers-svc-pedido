package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.entities.Pedido;

/**
 * Interface/Port para envio de informações ao painel de pedidos (senha)
 * Este é um sistema externo, a camada de infraestrutura deverá prover uma implementação
 * adequada para a comunicação com o mesmo
 */
public interface PainelPedidos {
    void notificarPedidoPronto(Pedido pedido);
}
