package com.example.fiap.archburgers.integration;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;

public class PedidoStepDefinitions {
    private int idPedidoRecebido;

    private Response response;

    @Dado("um cliente realizou e finalizou o pagamento de um pedido")
    public void um_cliente_realizou_e_finalizou_o_pagamento_de_um_pedido() {
        RestAssured.baseURI = "http://localhost:8091";
        RestAssured.basePath = "/pedidos";

        idPedidoRecebido = 1; // Gerado na carga inicial do banco de dados
    }

    @Dado("este pedido está com status Recebido")
    public void estePedidoEstáComStatusRecebido() {
        Response consultaResponse = given()
                .basePath("/pedidos/" + idPedidoRecebido)
                .get();

        consultaResponse.then()
                .statusCode(200)
                .body("", isA(Map.class),
                        "id", is(idPedidoRecebido),
                        "status", is("RECEBIDO")
                );
    }

    @Quando("o chefe de cozinha valida o pedido")
    public void o_chefe_de_cozinha_valida_o_pedido() {
        response = given()
                .basePath("/pedidos/" + idPedidoRecebido + "/validar")
                .contentType(ContentType.JSON)
                .body("{}")
                .post();
    }

    @Então("a resposta é um pedido com status Em Preparação")
    public void a_resposta_é_um_pedido_com_status_em_preparação() {
        response.then().body(
                "", isA(Map.class),
                "id", is(idPedidoRecebido),
                "status", is("PREPARACAO")
        );
    }
}
