package com.example.fiap.archburgers.di;

import com.example.fiap.archburgers.adapters.externalsystem.ProvedorAutenticacaoCognito;
import com.example.fiap.archburgers.controller.CarrinhoController;
import com.example.fiap.archburgers.controller.ClienteController;
import com.example.fiap.archburgers.controller.PedidoController;
import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.datagateway.PedidoGateway;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosLocal;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosService;
import com.example.fiap.archburgers.domain.external.PagamentoService;
import com.example.fiap.archburgers.domain.external.PainelPedidos;
import com.example.fiap.archburgers.domain.utils.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainServiceBeans {

    @Bean
    Clock clock() {
        return new Clock();
    }

    @Bean
    public CatalogoProdutosLocal catalogoProdutosLocal(CatalogoProdutosService catalogoProdutosService) {
        return new CatalogoProdutosLocal(catalogoProdutosService);
    }

    @Bean
    public ClienteController clienteController(ClienteGateway clienteGateway,
                                               ProvedorAutenticacaoCognito provedorAutenticacaoCognito) {
        return new ClienteController(clienteGateway, provedorAutenticacaoCognito);
    }

    @Bean
    public CarrinhoController carrinhoController(CarrinhoGateway carrinhoGateway,
                                                 ClienteGateway clienteGateway,
                                                 CatalogoProdutosLocal catalogoProdutosLocal,
                                                 Clock clock) {
        return new CarrinhoController(carrinhoGateway, clienteGateway, catalogoProdutosLocal, clock);
    }

    @Bean
    public PedidoController pedidoController(CarrinhoGateway carrinhoGateway,
                                             PedidoGateway pedidoGateway,
                                             ClienteGateway clienteGateway,
                                             CatalogoProdutosLocal catalogoProdutosLocal,
                                             PagamentoService pagamentoService,
                                             Clock clock,
                                             PainelPedidos painelPedidos) {
        return new PedidoController(pedidoGateway, carrinhoGateway, clienteGateway,
                catalogoProdutosLocal, pagamentoService, clock, painelPedidos);
    }
}
