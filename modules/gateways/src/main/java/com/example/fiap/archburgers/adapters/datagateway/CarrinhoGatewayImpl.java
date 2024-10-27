package com.example.fiap.archburgers.adapters.datagateway;

import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datasource.CarrinhoDataSource;
import com.example.fiap.archburgers.domain.entities.Carrinho;
import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.valueobjects.IdCliente;
import org.springframework.stereotype.Service;

@Service
public class CarrinhoGatewayImpl implements CarrinhoGateway {
    private final CarrinhoDataSource carrinhoDataSource;

    public CarrinhoGatewayImpl(CarrinhoDataSource carrinhoDataSource) {
        this.carrinhoDataSource = carrinhoDataSource;
    }

    @Override
    public Carrinho getCarrinhoSalvoByCliente(IdCliente idCliente) {
        return carrinhoDataSource.getCarrinhoSalvoByCliente(idCliente);
    }

    @Override
    public Carrinho getCarrinho(int idCarrinho) {
        return carrinhoDataSource.getCarrinho(idCarrinho);
    }

    @Override
    public Carrinho salvarCarrinhoVazio(Carrinho carrinho) {
        return carrinhoDataSource.salvarCarrinhoVazio(carrinho);
    }

    @Override
    public void salvarItemCarrinho(Carrinho carrinho, ItemPedido newItem) {
        carrinhoDataSource.salvarItemCarrinho(carrinho, newItem);
    }

    @Override
    public void deleteCarrinho(Carrinho carrinho) {
        carrinhoDataSource.deleteCarrinho(carrinho);
    }

    @Override
    public void updateObservacaoCarrinho(Carrinho carrinho) {
        carrinhoDataSource.updateObservacaoCarrinho(carrinho);
    }

    @Override
    public void deleteItensCarrinho(Carrinho carrinho) {
        carrinhoDataSource.deleteItensCarrinho(carrinho);
    }
}
