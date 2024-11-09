package com.example.fiap.archburgers.adapters.externalsystem;

import com.example.fiap.archburgers.domain.external.CatalogoProdutosService;
import com.example.fiap.archburgers.domain.external.ItemCardapio;
import com.example.fiap.archburgers.domain.utils.StringUtils;
import com.example.fiap.archburgers.domain.valueobjects.ValorMonetario;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.jetbrains.annotations.VisibleForTesting;
import org.springframework.core.env.Environment;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Collection;

public class CatalogoProdutosServiceImpl implements CatalogoProdutosService {
    private final URI cardapioApiUri;

    private final HttpClient httpClient;

    @VisibleForTesting
    final ObjectMapper mapper = new ObjectMapper();

    public CatalogoProdutosServiceImpl(Environment environment) {
        String cardapioApiUrlEnv = environment.getProperty("archburgers.integration.cardapio.ApiUrl");

        if (StringUtils.isEmpty(cardapioApiUrlEnv))
            throw new IllegalArgumentException("archburgers.integration.cardapio.ApiUrl not set");

        try {
            this.cardapioApiUri = new URI(cardapioApiUrlEnv);
        } catch (URISyntaxException e) {
            throw new RuntimeException("archburgers.integration.cardapio.ApiUrl is invalid: [" + cardapioApiUrlEnv + "] " + e);
        }

        this.httpClient = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(ValorMonetario.class, new ValorMonetarioDeserializer());
        mapper.registerModule(module);
    }

    @Override
    public Collection<ItemCardapio> findAll() {

        var webRequest = HttpRequest.newBuilder()
                .uri(cardapioApiUri)
                .timeout(Duration.ofMinutes(1))
                .header("Content-Type", "application/json")
                .GET()
                .build();

        HttpResponse<String> response;
        try {
            response = httpClient.send(webRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException("Error sending order request: " + e, e);
        }

        String body = response.body();

        if (response.statusCode() != 200) {
            throw new RuntimeException("Error in cardapio request: " + response + " -- " + body);
        }

        try {
            return mapper.readValue(body, new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Dados de cardápio inválidos! " + e, e);
        }
    }

    @VisibleForTesting
    private static class ValorMonetarioDeserializer extends JsonDeserializer<ValorMonetario> {
        @Override
        public ValorMonetario deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            if (!node.has("raw"))
                throw new RuntimeException("Invalid valor object. Missing 'raw' attr");
            if (!node.get("raw").isTextual())
                throw new RuntimeException("Invalid valor object. Invalid 'raw' attr");
            String valorStr = node.get("raw").asText();
            return new ValorMonetario(new BigDecimal(valorStr));
        }
    }
}
