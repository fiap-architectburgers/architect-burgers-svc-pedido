package com.example.fiap.archburgers.adapters.externalsystem;

import com.example.fiap.archburgers.domain.entities.Pedido;
import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.external.PagamentoService;
import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class PagamentoServiceImpl implements PagamentoService {

    private final PagamentoServiceWebApi webApi;
    private final PagamentoServiceQueueApi queueApi;

    private volatile List<IdFormaPagamento> formasPagamentoCache;
    private volatile Instant formasPagamentoCacheTimestamp;
    private final long webCacheMillis;

    @Autowired
    public PagamentoServiceImpl(PagamentoServiceWebApi webApi, PagamentoServiceQueueApi queueApi) {
        this.webApi = webApi;
        this.queueApi = queueApi;
        this.webCacheMillis = (5 * 60 * 1000);
    }

    @VisibleForTesting
    PagamentoServiceImpl(PagamentoServiceWebApi webApi, PagamentoServiceQueueApi queueApi, long webCacheMillis) {
        this.webApi = webApi;
        this.queueApi = queueApi;
        this.webCacheMillis = webCacheMillis;
    }

    @Override
    public IdFormaPagamento validarFormaPagamento(String idFormaPagamento) {
            List<IdFormaPagamento> formasPagamento = getCachedFormasPagamento();
            var formaPagamentoValida = formasPagamento.stream()
                    .filter(forma -> forma.id().equals(idFormaPagamento))
                    .findFirst();

            if (formaPagamentoValida.isPresent()) {
                return formaPagamentoValida.get();
            } else {
                throw new DomainArgumentException("Forma de pagamento desconhecida: " + idFormaPagamento);
            }
    }

    @Override
    public void iniciarPagamento(Pedido pedido) {

    }

    protected List<IdFormaPagamento> getCachedFormasPagamento () {
        if (formasPagamentoCache == null || Duration.between(formasPagamentoCacheTimestamp, Instant.now()).toMillis() >= webCacheMillis) {
            formasPagamentoCacheTimestamp = Instant.now();
            formasPagamentoCache = webApi.listFormasPagamento();
        }
        return formasPagamentoCache;
    }
}
