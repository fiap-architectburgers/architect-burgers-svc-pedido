package com.example.fiap.archburgers.adapters.datagateway;

import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.datasource.ClienteDataSource;
import com.example.fiap.archburgers.domain.entities.Cliente;
import com.example.fiap.archburgers.domain.valueobjects.Cpf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteGatewayImpl implements ClienteGateway {
    private final ClienteDataSource clienteDataSource;

    public ClienteGatewayImpl(ClienteDataSource clienteDataSource) {
        this.clienteDataSource = clienteDataSource;
    }

    @Override
    public @Nullable Cliente getClienteByCpf(@NotNull Cpf cpf) {
        return clienteDataSource.getClienteByCpf(cpf);
    }

    @Override
    public Cliente getClienteById(int id) {
        return clienteDataSource.getClienteById(id);
    }

    @Override
    public Cliente salvarCliente(@NotNull Cliente cliente) {
        return clienteDataSource.salvarCliente(cliente);
    }

    @Override
    public List<Cliente> listarTodosClientes() {
        return clienteDataSource.listarTodosClientes();
    }
}
