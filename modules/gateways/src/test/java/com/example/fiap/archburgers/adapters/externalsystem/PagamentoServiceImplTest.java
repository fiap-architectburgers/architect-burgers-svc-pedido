package com.example.fiap.archburgers.adapters.externalsystem;

import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class PagamentoServiceImplTest {

    private PagamentoServiceWebApi webApi;
    private PagamentoServiceQueueApi queueApi;
    private PagamentoServiceImpl pagamentoServiceImpl;

    @BeforeEach
    void setup(){
        webApi = mock(PagamentoServiceWebApi.class);
        queueApi = mock(PagamentoServiceQueueApi.class);
        pagamentoServiceImpl = new PagamentoServiceImpl(webApi, queueApi);
    }

    @Test
    void validarFormaPagamento_valido() {
        IdFormaPagamento formaPagamentoValid = new IdFormaPagamento("valid");
        when(webApi.listFormasPagamento()).thenReturn(Collections.singletonList(formaPagamentoValid));

        IdFormaPagamento returnedFormaPagamento = pagamentoServiceImpl.validarFormaPagamento("valid");

        assertSame(formaPagamentoValid, returnedFormaPagamento);
        verify(webApi, times(1)).listFormasPagamento();
    }

    @Test
    void validarFormaPagamento_checkUsesCache() throws Exception {
        pagamentoServiceImpl = new PagamentoServiceImpl(webApi, queueApi, 200L);

        IdFormaPagamento formaPagamentoValid = new IdFormaPagamento("valid");
        when(webApi.listFormasPagamento()).thenReturn(Collections.singletonList(formaPagamentoValid));

        pagamentoServiceImpl.validarFormaPagamento("valid");
        pagamentoServiceImpl.validarFormaPagamento("valid");
        try {
            pagamentoServiceImpl.validarFormaPagamento("invalid");
        } catch (Exception ignored) {
        }
        pagamentoServiceImpl.validarFormaPagamento("valid");

        // external API invoked only once until now
        verify(webApi, times(1)).listFormasPagamento();

        // wait cache expiration before performing new request
        Thread.sleep(250L);

        pagamentoServiceImpl.validarFormaPagamento("valid");
        verify(webApi, times(2)).listFormasPagamento();
    }

    @Test
    void validarFormaPagamento_invalido() {
        when(webApi.listFormasPagamento()).thenReturn(List.of(new IdFormaPagamento("valid")));

        assertThatThrownBy(() -> pagamentoServiceImpl.validarFormaPagamento("invalid"))
                .isInstanceOf(DomainArgumentException.class)
                .hasMessage("Forma de pagamento desconhecida: invalid");
    }
}