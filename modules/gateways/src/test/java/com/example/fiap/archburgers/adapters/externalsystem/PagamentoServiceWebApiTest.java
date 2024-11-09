package com.example.fiap.archburgers.adapters.externalsystem;

import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PagamentoServiceWebApiTest {

    @Mock
    private Environment environment;

    @Test
    public void listFormasPagamentoThrowsExceptionTest() {
        when(environment.getProperty("archburgers.integration.pagamento.ApiUrl"))
                .thenReturn("");
        Assertions.assertThrows(IllegalArgumentException.class, ()-> new PagamentoServiceWebApi(environment));
    }

    @Test
    public void listFormasPagamentoInvalidApiUrlTest() {
        when(environment.getProperty("archburgers.integration.pagamento.ApiUrl"))
                .thenReturn("##########");
        Assertions.assertThrows(RuntimeException.class, ()-> new PagamentoServiceWebApi(environment));
    }
}