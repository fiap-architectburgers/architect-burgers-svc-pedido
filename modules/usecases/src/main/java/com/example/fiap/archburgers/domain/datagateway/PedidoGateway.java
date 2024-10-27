package com.example.fiap.archburgers.domain.datagateway;

import com.example.fiap.archburgers.domain.entities.Pedido;
import com.example.fiap.archburgers.domain.valueobjects.StatusPedido;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;

public interface PedidoGateway {
    Pedido getPedido(int idPedido);

    Pedido savePedido(Pedido pedido);

    List<Pedido> listPedidos(List<StatusPedido> filtroStatus,
                             @Nullable LocalDateTime olderThan);

    void updateStatus(Pedido pedido);

    void excluirPedido(int idPedido);
}
