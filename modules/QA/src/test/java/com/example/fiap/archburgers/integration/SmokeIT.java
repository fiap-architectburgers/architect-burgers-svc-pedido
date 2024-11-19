package com.example.fiap.archburgers.integration;

import io.restassured.RestAssured;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.isA;

/**
 * A basic end-to-end test ("smoke test") which verifies if the application is up with all of its dependencies.
 */
public class SmokeIT {
    @Test
    public void getClientes_pureDatabaseQuery() {
        RestAssured.when().
                get("http://localhost:8091/clientes").
                then().
                statusCode(200).
                body("", Matchers.isA(List.class),
                        "[0].nome", equalTo("Roberto Carlos"));
    }

    @Test
    public void getPedido_integratesWithCatalogoApi() {
        // Verifica existência dos dados do Pedido que foram enriquecidos através da api do serviço de catálogo

        RestAssured.when().
                get("http://localhost:8091/pedidos/2").
                then().
                statusCode(200).
                body("", Matchers.isA(Map.class),
                        "itens", Matchers.isA(List.class),
                        "itens[0].idItemCardapio", isA(Integer.class),
                        "itens[0].nome", isA(String.class),
                        "itens[0].descricao", isA(String.class));
    }
}
