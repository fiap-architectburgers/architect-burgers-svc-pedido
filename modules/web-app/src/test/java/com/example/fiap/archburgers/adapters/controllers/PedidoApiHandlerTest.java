package com.example.fiap.archburgers.adapters.controllers;//import static org.junit.jupiter.api.Assertions.*;

import com.example.fiap.archburgers.adapters.auth.UsuarioLogadoTokenParser;
import com.example.fiap.archburgers.adapters.dto.ItemPedidoDto;
import com.example.fiap.archburgers.adapters.dto.PedidoDto;
import com.example.fiap.archburgers.adapters.dto.ValorMonetarioDto;
import com.example.fiap.archburgers.adapters.testUtils.DummyTransactionManager;
import com.example.fiap.archburgers.adapters.testUtils.TestLocale;
import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.entities.Pedido;
import com.example.fiap.archburgers.domain.exception.DomainPermissionException;
import com.example.fiap.archburgers.domain.external.ItemCardapio;
import com.example.fiap.archburgers.domain.usecaseparam.CriarPedidoParam;
import com.example.fiap.archburgers.domain.usecases.PedidoUseCases;
import com.example.fiap.archburgers.domain.valueobjects.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class PedidoApiHandlerTest {


    @Mock
    private PedidoUseCases pedidoUseCases;
    @Mock
    private UsuarioLogadoTokenParser usuarioLogadoTokenParser;

    private MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() {
        TestLocale.setDefault();
    }

    @BeforeEach
    public void setup() {
        PedidoApiHandler pedidoApiHandler = new PedidoApiHandler(pedidoUseCases, usuarioLogadoTokenParser, new DummyTransactionManager());
        mockMvc = MockMvcBuilders.standaloneSetup(pedidoApiHandler).build();
    }

}
