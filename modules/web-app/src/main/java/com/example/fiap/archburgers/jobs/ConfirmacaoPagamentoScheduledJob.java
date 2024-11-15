package com.example.fiap.archburgers.jobs;

import com.example.fiap.archburgers.domain.external.Pagamento;
import com.example.fiap.archburgers.domain.external.PagamentoService;
import com.example.fiap.archburgers.domain.usecases.PedidoUseCases;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
public class ConfirmacaoPagamentoScheduledJob implements Consumer<Pagamento> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfirmacaoPagamentoScheduledJob.class);

    private final PagamentoService pagamentoService;
    private final PedidoUseCases pedidoUseCases;

    public ConfirmacaoPagamentoScheduledJob(PagamentoService pagamentoService, PedidoUseCases pedidoUseCases) {
        this.pagamentoService = pagamentoService;
        this.pedidoUseCases = pedidoUseCases;
    }

    @Scheduled(fixedDelay = 5000)
    public void readConfirmacaoPagamento() {
        try {
            pagamentoService.receberConfirmacoes(this);
        } catch (Exception e) {
            LOGGER.error("Erro tratando confirmações de pagamento! {}", e, e);
        }
    }

    @Override
    public void accept(Pagamento pagamento) {
        pedidoUseCases.finalizarPagamento(pagamento);
    }
}
