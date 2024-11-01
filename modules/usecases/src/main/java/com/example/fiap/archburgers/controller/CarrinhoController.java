package com.example.fiap.archburgers.controller;

import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosLocal;
import com.example.fiap.archburgers.domain.usecaseparam.CriarCarrinhoParam;
import com.example.fiap.archburgers.domain.usecases.CarrinhoUseCases;
import com.example.fiap.archburgers.domain.utils.Clock;
import com.example.fiap.archburgers.domain.valueobjects.CarrinhoDetalhe;
import org.jetbrains.annotations.NotNull;

public class CarrinhoController {
    private final CarrinhoUseCases carrinhoUseCases;

    public CarrinhoController(CarrinhoGateway carrinhoGateway,
                              ClienteGateway clienteGateway,
                              CatalogoProdutosLocal catalogoProdutosLocal,
                              Clock clock) {
        this.carrinhoUseCases = new CarrinhoUseCases(carrinhoGateway, clienteGateway, catalogoProdutosLocal, clock);
    }

    public CarrinhoDetalhe findCarrinho(int idCarrinho) {
        return carrinhoUseCases.findCarrinho(idCarrinho);
    }

    public CarrinhoDetalhe criarCarrinho(@NotNull CriarCarrinhoParam param, @NotNull UsuarioLogado usuarioLogado) {
        return carrinhoUseCases.criarCarrinho(param, usuarioLogado);
    }

    public CarrinhoDetalhe addItem(int idCarrinho, int idItemCardapio) {
        return carrinhoUseCases.addItem(idCarrinho, idItemCardapio);
    }

    public CarrinhoDetalhe deleteItem(int idCarrinho, int numSequencia) {
        return carrinhoUseCases.deleteItem(idCarrinho, numSequencia);
    }

    public CarrinhoDetalhe setObservacoes(int idCarrinho, String textoObservacao) {
        return carrinhoUseCases.setObservacoes(idCarrinho, textoObservacao);
    }

}
