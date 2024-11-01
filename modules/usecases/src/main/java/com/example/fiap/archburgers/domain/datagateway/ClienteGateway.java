package com.example.fiap.archburgers.domain.datagateway;

import com.example.fiap.archburgers.domain.entities.Cliente;
import com.example.fiap.archburgers.domain.valueobjects.Cpf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ClienteGateway {
    @Nullable
    Cliente getClienteByCpf(@NotNull Cpf cpf);

    Cliente getClienteById(int id);

    Cliente salvarCliente(@NotNull Cliente cliente);

    List<Cliente> listarTodosClientes();
}