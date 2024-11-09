package com.example.fiap.archburgers.domain.entities;//import static org.junit.jupiter.api.Assertions.*;

import com.example.fiap.archburgers.domain.valueobjects.Cpf;
import com.example.fiap.archburgers.domain.valueobjects.IdCliente;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ClienteTest {

    @Test
    void attributes() {
        Cliente cliente = new Cliente(new IdCliente(123), "Roberto Carlos",
                new Cpf("12332112340"), "roberto.carlos@example.com");

        Assertions.assertThat(cliente.id()).isEqualTo(new IdCliente(123));
        Assertions.assertThat(cliente.nome()).isEqualTo("Roberto Carlos");
        Assertions.assertThat(cliente.cpf()).isEqualTo(new Cpf("12332112340"));
        Assertions.assertThat(cliente.email()).isEqualTo("roberto.carlos@example.com");
    }
}