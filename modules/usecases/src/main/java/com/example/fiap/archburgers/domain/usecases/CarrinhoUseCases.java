package com.example.fiap.archburgers.domain.usecases;

import com.example.fiap.archburgers.domain.auth.UsuarioLogado;
import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.entities.Carrinho;
import com.example.fiap.archburgers.domain.entities.ItemCardapio;
import com.example.fiap.archburgers.domain.entities.ItemPedido;
import com.example.fiap.archburgers.domain.exception.DomainArgumentException;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosLocal;
import com.example.fiap.archburgers.domain.usecaseparam.CriarCarrinhoParam;
import com.example.fiap.archburgers.domain.utils.Clock;
import com.example.fiap.archburgers.domain.utils.StringUtils;
import com.example.fiap.archburgers.domain.valueobjects.CarrinhoDetalhe;
import com.example.fiap.archburgers.domain.valueobjects.Cpf;
import com.example.fiap.archburgers.domain.valueobjects.IdCliente;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;

public class CarrinhoUseCases {

    private final CarrinhoGateway carrinhoGateway;
    private final ClienteGateway clienteGateway;
    private final CatalogoProdutosLocal catalogo;
    private final Clock clock;

    private final RecuperarCarrinhoPolicy recuperarCarrinhoPolicy;

    public CarrinhoUseCases(CarrinhoGateway carrinhoGateway,
                            ClienteGateway clienteGateway,
                            CatalogoProdutosLocal catalogoProdutosLocal,
                            Clock clock) {
        this.carrinhoGateway = carrinhoGateway;
        this.clienteGateway = clienteGateway;
        this.catalogo = catalogoProdutosLocal;
        this.clock = clock;

        this.recuperarCarrinhoPolicy = new RecuperarCarrinhoPolicy();
    }

    public CarrinhoDetalhe criarCarrinho(@NotNull CriarCarrinhoParam param, @NotNull UsuarioLogado usuarioLogado) {
        IdCliente idClienteLogado;
        if (usuarioLogado.autenticado()) {
            var clienteRegistrado = clienteGateway.getClienteByCpf(new Cpf(usuarioLogado.getCpf()));
            if (clienteRegistrado == null)
                throw new RuntimeException("Registro inconsistente! Usuario logado [" + usuarioLogado.getCpf() + "] não cadastrado na base");
            idClienteLogado = clienteRegistrado.id();
        } else {
            idClienteLogado = null;
        }

        if (idClienteLogado != null) {
            var carrinhoSalvo = recuperarCarrinhoPolicy.tryRecuperarCarrinho(idClienteLogado);
            if (carrinhoSalvo != null) {
                Map<Integer, ItemCardapio> detalheItens = catalogo.findAll(carrinhoSalvo.itens());
                return new CarrinhoDetalhe(carrinhoSalvo, detalheItens);
            }
        }

        Carrinho newCarrinho;
        if (idClienteLogado != null) {
            newCarrinho = Carrinho.newCarrinhoVazioClienteIdentificado(
                    idClienteLogado, clock.localDateTime());
        } else {
            if (StringUtils.isEmpty(param.nomeCliente()))
                throw new DomainArgumentException("Cliente não autenticado deve informar o nomeCliente");

            newCarrinho = Carrinho.newCarrinhoVazioClienteNaoIdentificado(param.nomeCliente(), clock.localDateTime());
        }

        return new CarrinhoDetalhe(carrinhoGateway.salvarCarrinhoVazio(newCarrinho), Collections.emptyMap());
    }

    public CarrinhoDetalhe addItem(int idCarrinho, int idItemCardapio) {
        var carrinho = carrinhoGateway.getCarrinho(idCarrinho);
        if (carrinho == null) {
            throw new IllegalArgumentException("Carrinho invalido! " + idCarrinho);
        }

        var itemCardapio = catalogo.findById(idItemCardapio);
        if (itemCardapio == null) {
            throw new IllegalArgumentException("Item cardapio invalido! " + idItemCardapio);
        }

        var newCarrinho = carrinho.adicionarItem(itemCardapio);

        ItemPedido newItem = newCarrinho.itens().getLast();

        carrinhoGateway.salvarItemCarrinho(newCarrinho, newItem);

        return new CarrinhoDetalhe(newCarrinho, catalogo.findAll(newCarrinho.itens()));
    }

    public CarrinhoDetalhe deleteItem(int idCarrinho, int numSequencia) {
        var carrinho = carrinhoGateway.getCarrinho(idCarrinho);
        if (carrinho == null) {
            throw new IllegalArgumentException("Carrinho invalido! " + idCarrinho);
        }

        carrinho = carrinho.deleteItem(numSequencia);

        carrinhoGateway.deleteItensCarrinho(carrinho);
        for (ItemPedido item : carrinho.itens()) {
            carrinhoGateway.salvarItemCarrinho(carrinho, item);
        }

        return new CarrinhoDetalhe(carrinho, catalogo.findAll(carrinho.itens()));
    }

    public CarrinhoDetalhe setObservacoes(int idCarrinho, String textoObservacao) {
        var carrinho = carrinhoGateway.getCarrinho(idCarrinho);
        if (carrinho == null) {
            throw new IllegalArgumentException("Carrinho invalido! " + idCarrinho);
        }

        var newCarrinho = carrinho.setObservacoes(textoObservacao);

        carrinhoGateway.updateObservacaoCarrinho(newCarrinho);

        return new CarrinhoDetalhe(newCarrinho, catalogo.findAll(newCarrinho.itens()));
    }

    public CarrinhoDetalhe findCarrinho(int idCarrinho) {
        var carrinho = carrinhoGateway.getCarrinho(idCarrinho);
        if (carrinho == null) {
            throw new IllegalArgumentException("Carrinho invalido! " + idCarrinho);
        }

        return new CarrinhoDetalhe(carrinho, catalogo.findAll(carrinho.itens()));
    }

    /**
     * Política do fluxo de compra: Se o cliente se identificou e há um carrinho salvo,
     * carrega para permitir continuar a compra
     */
    private class RecuperarCarrinhoPolicy {
        Carrinho tryRecuperarCarrinho(IdCliente idCliente) {
            return carrinhoGateway.getCarrinhoSalvoByCliente(idCliente);
        }
    }
}
