package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.entities.Cliente;

/**
 * Interface para sistema de autenticação / registro de usuários
 */
public interface ProvedorAutenticacaoExterno {
    void registrarCliente(Cliente cliente, String senha);
}
