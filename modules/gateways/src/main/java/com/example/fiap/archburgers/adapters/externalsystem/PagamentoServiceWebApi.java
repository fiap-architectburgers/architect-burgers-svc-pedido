package com.example.fiap.archburgers.adapters.externalsystem;

import com.example.fiap.archburgers.domain.utils.StringUtils;
import com.example.fiap.archburgers.domain.valueobjects.IdFormaPagamento;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

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

    public List<IdFormaPagamento> listFormasPagamento() {
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

        List<IdFormaPagamento> formasPagamento;
        try {
            formasPagamento = mapper.readValue(body, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Dados de forma de pagamento inválidos! " + e, e);
        }

        return formasPagamento;
    }
}
