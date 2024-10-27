package com.example.fiap.archburgers.domain.external;//import static org.junit.jupiter.api.Assertions.*;

import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FormaPagamentoRegistryTest {
    private FormaPagamento pagamentoExterno1;
    private FormaPagamento pagamentoExterno2;

    private FormaPagamentoRegistry formaPagamentoRegistry;

    @BeforeEach
    void setUp() {
        pagamentoExterno1 = mock();
        when(pagamentoExterno1.id()).thenReturn(new IdFormaPagamento("PagamentoExterno1"));

        pagamentoExterno2 = mock();
        when(pagamentoExterno2.id()).thenReturn(new IdFormaPagamento("PagamentoExterno2"));

        formaPagamentoRegistry = new FormaPagamentoRegistry(List.of(pagamentoExterno1, pagamentoExterno2));
    }

    @Test
    void formasPagamentoCore() {
        FormaPagamento dinheiro = formaPagamentoRegistry.getFormaPagamento(IdFormaPagamento.DINHEIRO);
        assertThat(dinheiro.id()).isEqualTo(IdFormaPagamento.DINHEIRO);
        assertThat(dinheiro.descricao()).isEqualTo("Pagamento em dinheiro direto ao caixa");
        assertThat(dinheiro.isIntegracaoExterna()).isFalse();

        FormaPagamento cartao = formaPagamentoRegistry.getFormaPagamento(IdFormaPagamento.CARTAO_MAQUINA);
        assertThat(cartao.id()).isEqualTo(IdFormaPagamento.CARTAO_MAQUINA);
        assertThat(cartao.descricao()).isEqualTo("Pagamento na máquina da loja");
        assertThat(cartao.isIntegracaoExterna()).isFalse();
    }

    @Test
    void formasPagamentoExternas() {
        FormaPagamento pag1 = formaPagamentoRegistry.getFormaPagamento(new IdFormaPagamento("PagamentoExterno1"));
        assertThat(pag1).isSameAs(pagamentoExterno1);

        FormaPagamento pag2 = formaPagamentoRegistry.getFormaPagamento(new IdFormaPagamento("PagamentoExterno2"));
        assertThat(pag2).isSameAs(pagamentoExterno2);
    }

    @Test
    void formaPagamentoInvalida() {
        var e = assertThrows(DomainArgumentException.class, () -> formaPagamentoRegistry.getFormaPagamento(
                new IdFormaPagamento("Forma_xyz")
        ));
        assertThat(e).hasMessage("Forma de pagamento desconhecida: Forma_xyz");
    }
}