package com.example.fiap.archburgers.integration;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class CarrinhoStepDefinitions {

    private String nomeCliente;

    private Response response;

    @Dado("que um cliente está iniciando sua compra")
    public void que_um_cliente_está_iniciando_sua_compra() {
        RestAssured.baseURI = "http://localhost:8091";
        RestAssured.basePath = "/carrinho";
    }

    @Quando("o cliente começa um novo pedido informando seu nome como identificação")
    public void o_cliente_começa_um_novo_pedido_informando_seu_nome_como_identificação() {
        nomeCliente = "Wilson";

        response = given()
                .contentType(ContentType.JSON)
                .body("{\"nomeCliente\": \"" + nomeCliente + "\"}")
                .post();
    }

    @Então("a resposta é um carrinho de compras")
    public void a_resposta_é_um_carrinho_de_compras() {
        response.then().body(
                "", isA(Map.class),
                "id", isA(Integer.class)
        );
    }

    @Então("o carrinho está vazio")
    public void o_carrinho_está_vazio() {
        response.then().body(
                "itens", isA(List.class),
                "itens", hasSize(0));
    }

    @Então("o carrinho contém o nome do cliente")
    public void o_carrinho_contém_o_nome_do_cliente() {
        response.then().body(
                "nomeClienteNaoIdentificado", is(nomeCliente)
        );
    }

    // WIP
//    @Dado("que um cliente possui um carrinho de compras")
//    public void que_um_cliente_possui_um_carrinho_de_compras() {
//        nomeCliente = "Wanderléia";
//
//        var carrinhoResponse = given()
//                .contentType(ContentType.JSON)
//                .body("{\"nomeCliente\": \"" + nomeCliente + "\"}")
//                .post();
//
//        idCarrinho = carrinhoResponse.then()
//                .body(
//                        "", isA(Map.class),
//                        "id", isA(Integer.class)
//                )
//                .extract().path("id");
//    }
//
//    @Quando("o cliente adiciona um item do cardápio")
//    public void o_cliente_adiciona_um_item_do_cardápio() {
//        itemCardapioSelecionado = 5;
//
//        response = given()
//                .basePath("/carrinho/" + idCarrinho)
//                .contentType(ContentType.JSON)
//                .body("{\"idItemCardapio\": \"" + 5 + "\"}")
//                .post();
//    }
//
//    @Então("o carrinho contém em seus itens o produto selecionado")
//    public void o_carrinho_contém_em_seus_itens_o_produto_selecionado() {
//        // Write code here that turns the phrase above into concrete actions
//        throw new io.cucumber.java.PendingException();
//    }

}
