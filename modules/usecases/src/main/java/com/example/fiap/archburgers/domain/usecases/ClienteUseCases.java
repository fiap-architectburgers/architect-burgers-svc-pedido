package com.example.fiap.archburgers.domain.usecases;

import com.example.fiap.archburgers.domain.auth.GrupoUsuario;
import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.entities.Cliente;
import com.example.fiap.archburgers.domain.exception.DomainPermissionException;
import com.example.fiap.archburgers.domain.external.ProvedorAutenticacaoExterno;
import com.example.fiap.archburgers.domain.usecaseparam.CadastrarClienteParam;
import com.example.fiap.archburgers.domain.valueobjects.Cpf;

import java.util.List;

public class ClienteUseCases {
    private final ClienteGateway clienteGateway;
    private final ProvedorAutenticacaoExterno provedorAutenticacaoExterno;

    public ClienteUseCases(ClienteGateway clienteGateway, ProvedorAutenticacaoExterno provedorAutenticacaoExterno) {
        this.clienteGateway = clienteGateway;
        this.provedorAutenticacaoExterno = provedorAutenticacaoExterno;
    }

    public Cliente getClienteByCredencial(UsuarioLogado usuarioLogado) throws DomainPermissionException {
        if (!usuarioLogado.autenticado())
            throw new DomainPermissionException("Usuário logado inválido. " + usuarioLogado.authError());

        if (usuarioLogado.getGrupo() != GrupoUsuario.ClienteCadastrado)
            throw new DomainPermissionException("Usuário logado inválido. Não pertence ao grupo " + GrupoUsuario.ClienteCadastrado);

        var cadastrado = clienteGateway.getClienteByCpf(new Cpf(usuarioLogado.getCpf()));

        if (cadastrado == null) {
            throw new RuntimeException("Registro inconsistente. Cliente não cadastrado na base");
        }

        return cadastrado;
    }

    public List<Cliente> listTodosClientes() {
        return clienteGateway.listarTodosClientes();
    }

    public Cliente cadastrarCliente(CadastrarClienteParam param) {
        param.validar();

        Cliente checkExistente = clienteGateway.getClienteByCpf(param.getCpfValidado());
        if (checkExistente != null) {
            throw new IllegalArgumentException("Cliente com CPF " + param.cpf() + " já cadastrado");
        }
        //TODO Também checar duplicidade de email

        Cliente newCliente = new Cliente(null, param.nome(), param.getCpfValidado(), param.email());
        Cliente saved = clienteGateway.salvarCliente(newCliente);

        provedorAutenticacaoExterno.registrarCliente(saved, param.senha());

        return saved;
    }
}
