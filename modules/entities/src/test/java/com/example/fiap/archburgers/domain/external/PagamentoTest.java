package com.example.fiap.archburgers.domain.external;

import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import com.example.fiap.archburgers.domain.valueobjects.StatusPagamento;
import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PagamentoTest {

    @Test
    void checkAttributes() {
        LocalDateTime dataHoraCriacao = LocalDateTime.now();
        LocalDateTime dataHoraAtualizacao = LocalDateTime.now();

        var pagamento = new Pagamento(44, 123,
                FORMA_PAGAMENTO_DINHEIRO, StatusPagamento.FINALIZADO,
                new ValorMonetario("19.90"), dataHoraCriacao,
                dataHoraAtualizacao, "barcode:xxx", "ABC");

        Assertions.assertThat(pagamento.id()).isEqualTo(44);
        Assertions.assertThat(pagamento.idPedido()).isEqualTo(123);
        Assertions.assertThat(pagamento.formaPagamento()).isEqualTo(FORMA_PAGAMENTO_DINHEIRO);
        Assertions.assertThat(pagamento.status()).isEqualTo(StatusPagamento.FINALIZADO);
        Assertions.assertThat(pagamento.valor()).isEqualTo(new ValorMonetario("19.90"));
        Assertions.assertThat(pagamento.dataHoraCriacao()).isEqualTo(dataHoraCriacao);
        Assertions.assertThat(pagamento.dataHoraAtualizacao()).isEqualTo(dataHoraAtualizacao);
        Assertions.assertThat(pagamento.codigoPagamentoCliente()).isEqualTo("barcode:xxx");
        Assertions.assertThat(pagamento.idPedidoSistemaExterno()).isEqualTo("ABC");
    }

    @Test
    void checkEqualsAndHashCode() {
        LocalDateTime dataHoraCriacao = LocalDateTime.now();
        LocalDateTime dataHoraAtualizacao = LocalDateTime.now();

        var pagamento1 = new Pagamento(44, 123,
                FORMA_PAGAMENTO_DINHEIRO, StatusPagamento.FINALIZADO,
                new ValorMonetario("19.90"), dataHoraCriacao,
                dataHoraAtualizacao, "barcode:xxx", "ABC");

        var pagamento2 = new Pagamento(44, 123,
                FORMA_PAGAMENTO_DINHEIRO, StatusPagamento.FINALIZADO,
                new ValorMonetario("19.90"), dataHoraCriacao,
                dataHoraAtualizacao, "barcode:xxx", "ABC");

        var pagamento3 = new Pagamento(55, 456,
                FORMA_PAGAMENTO_DINHEIRO, StatusPagamento.PENDENTE,
                new ValorMonetario("25.50"), LocalDateTime.now(),
                LocalDateTime.now(), "barcode:yyy", "DEF");

        Assertions.assertThat(pagamento1).isEqualTo(pagamento2);
        Assertions.assertThat(pagamento1).isNotEqualTo(pagamento3);
        Assertions.assertThat(pagamento1.hashCode()).isEqualTo(pagamento2.hashCode());
    }

    private static final IdFormaPagamento FORMA_PAGAMENTO_DINHEIRO = new IdFormaPagamento("DINHEIRO");
}