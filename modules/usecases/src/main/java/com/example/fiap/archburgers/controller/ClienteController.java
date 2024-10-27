package com.example.fiap.archburgers.controller;

import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.entities.Cliente;
import com.example.fiap.archburgers.domain.exception.DomainPermissionException;
import com.example.fiap.archburgers.domain.external.ProvedorAutenticacaoExterno;
import com.example.fiap.archburgers.domain.usecaseparam.CadastrarClienteParam;
import com.example.fiap.archburgers.domain.usecases.ClienteUseCases;

import java.util.List;

public class ClienteController {
    private final ClienteUseCases clienteUseCases;

    public ClienteController(ClienteGateway clienteGateway, ProvedorAutenticacaoExterno provedorAutenticacaoExterno) {
        clienteUseCases = new ClienteUseCases(clienteGateway, provedorAutenticacaoExterno);
    }

    public Cliente getClienteByCredencial(UsuarioLogado usuarioLogado)  throws DomainPermissionException {
        return clienteUseCases.getClienteByCredencial(usuarioLogado);
    }

    public List<Cliente> listTodosClientes() {
        return clienteUseCases.listTodosClientes();
    }

    public Cliente cadastrarCliente(CadastrarClienteParam param) {
        return clienteUseCases.cadastrarCliente(param);
    }
}
