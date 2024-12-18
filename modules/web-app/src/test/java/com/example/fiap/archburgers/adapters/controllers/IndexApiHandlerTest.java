package com.example.fiap.archburgers.adapters.controllers;//import static org.junit.jupiter.api.Assertions.*;

import com.example.fiap.archburgers.adapters.auth.UsuarioLogadoTokenParser;
import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class IndexApiHandlerTest {
    @Mock
    private UsuarioLogadoTokenParser usuarioLogadoTokenParser;

    private MockMvc mockMvc;

    private IndexApiHandler indexApiHandler;

    @BeforeEach
    void setUp() {
        indexApiHandler = new IndexApiHandler(usuarioLogadoTokenParser);

        mockMvc = MockMvcBuilders.standaloneSetup(indexApiHandler).build();
    }

    @Test
    void index() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }

    @Test
    void healthcheck() throws Exception {
        mockMvc.perform(get("/healthcheck"))
                .andExpect(status().isOk());
    }

    @Test
    void getClienteConectado_autenticado() throws Exception {
        UsuarioLogado usuarioLogado = mock(UsuarioLogado.class);
        when(usuarioLogado.autenticado()).thenReturn(true);

        when(usuarioLogado.getNome()).thenReturn("Alice");
        when(usuarioLogado.getEmail()).thenReturn("alice@example.com");
        when(usuarioLogado.getCpf()).thenReturn("11122233344");
        when(usuarioLogado.getGrupo()).thenReturn(null);
        when(usuarioLogado.identityToken()).thenReturn("AbcDefGhi");

        when(usuarioLogadoTokenParser.verificarUsuarioLogado(any())).thenReturn(usuarioLogado);
        mockMvc.perform(get("/usuario/conectado"))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                      "nome": "Alice",
                      "email": "alice@example.com",
                      "cpf": "11122233344",
                      "grupo": "",
                      "token": "AbcDefGhi"
                    }
                """
                ));
    }

    @Test
    void getClienteConectado_naoAutenticado() throws Exception {
        UsuarioLogado usuarioLogado = mock(UsuarioLogado.class);
        when(usuarioLogado.autenticado()).thenReturn(false);

        when(usuarioLogadoTokenParser.verificarUsuarioLogado(any())).thenReturn(usuarioLogado);
        mockMvc.perform(get("/usuario/conectado"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getClienteConectado_error() throws Exception {
        UsuarioLogado usuarioLogado = mock(UsuarioLogado.class);
        when(usuarioLogado.autenticado()).thenThrow(new RuntimeException("Something went wrong"));

        when(usuarioLogadoTokenParser.verificarUsuarioLogado(any())).thenReturn(usuarioLogado);
        mockMvc.perform(get("/usuario/conectado"))
                .andExpect(status().is5xxServerError());
    }
}