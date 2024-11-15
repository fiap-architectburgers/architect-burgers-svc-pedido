package com.example.fiap.archburgers.adapters.externalsystem;

import com.example.fiap.archburgers.domain.utils.StringUtils;
import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class PagamentoServiceWebApi {
    private final URI opcoesPagamentoUri;

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public PagamentoServiceWebApi(Environment environment) {
        String opcoesPagamentoApiUrlEnv = environment.getProperty("archburgers.integration.pagamento.ApiUrl");

        if (StringUtils.isEmpty(opcoesPagamentoApiUrlEnv))
            throw new IllegalArgumentException("archburgers.integration.pagamento.ApiUrl not set");

        try {
            this.opcoesPagamentoUri = new URI(opcoesPagamentoApiUrlEnv);
        } catch (URISyntaxException e) {
            throw new RuntimeException("archburgers.integration.pagamento.ApiUrl is invalid: [" + opcoesPagamentoApiUrlEnv + "] " + e);
        }

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public List<IdFormaPagamento> listFormasPagamento() throws Exception {
        var webRequest = HttpRequest.newBuilder()
                .uri(opcoesPagamentoUri)
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(webRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao solicitar formas de pagamento: " + e, e);
        }

        String body = response.body();

        if (response.statusCode() != 200) {
            throw new RuntimeException("Erro na solicitação de formas de pagamento: " + response + " -- " + body);
        }

        return parseFormasPagamentoResponse(body);
    }

    @VisibleForTesting
    List<IdFormaPagamento> parseFormasPagamentoResponse(String body) throws Exception {
        List<Map<?, ?>> rawData = mapper.readValue(body, new TypeReference<>() {
        });
        return rawData.stream().map(map -> {
            if (map.get("id") instanceof Map<?, ?> idAsMap) {
                if (idAsMap.get("codigo") instanceof String codigo) {
                    return new IdFormaPagamento(codigo);
                } else {
                    throw new RuntimeException("Unexpected opcoes-pagamento data format. codigo is missing");
                }
            } else {
                throw new RuntimeException("Unexpected opcoes-pagamento data format. id should be object");
            }
        }).toList();
    }
}
