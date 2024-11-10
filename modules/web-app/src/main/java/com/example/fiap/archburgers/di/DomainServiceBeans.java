package com.example.fiap.archburgers.di;

import com.example.fiap.archburgers.adapters.externalsystem.CatalogoProdutosServiceImpl;
import com.example.fiap.archburgers.adapters.externalsystem.ProvedorAutenticacaoCognito;
import com.example.fiap.archburgers.domain.datagateway.CarrinhoGateway;
import com.example.fiap.archburgers.domain.datagateway.ClienteGateway;
import com.example.fiap.archburgers.domain.datagateway.PedidoGateway;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosLocal;
import com.example.fiap.archburgers.domain.external.CatalogoProdutosService;
import com.example.fiap.archburgers.domain.external.PagamentoService;
import com.example.fiap.archburgers.domain.external.PainelPedidos;
import com.example.fiap.archburgers.domain.usecases.CarrinhoUseCases;
import com.example.fiap.archburgers.domain.usecases.ClienteUseCases;
import com.example.fiap.archburgers.domain.usecases.PedidoUseCases;
import com.example.fiap.archburgers.domain.utils.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class DomainServiceBeans {

    @Bean
    Clock clock() {
        return new Clock();
    }

    @Bean
    public CatalogoProdutosService catalogoProdutosService(Environment env) {
        return new CatalogoProdutosServiceImpl(env);
    }

    @Bean
    public CatalogoProdutosLocal catalogoProdutosLocal(CatalogoProdutosService catalogoProdutosService) {
        return new CatalogoProdutosLocal(catalogoProdutosService);
    }

    @Bean
    public ClienteUseCases clienteController(ClienteGateway clienteGateway,
                                             ProvedorAutenticacaoCognito provedorAutenticacaoCognito) {
        return new ClienteUseCases(clienteGateway, provedorAutenticacaoCognito);
    }

    @Bean
    public CarrinhoUseCases carrinhoController(CarrinhoGateway carrinhoGateway,
                                               ClienteGateway clienteGateway,
                                               CatalogoProdutosLocal catalogoProdutosLocal,
                                               Clock clock) {
        return new CarrinhoUseCases(carrinhoGateway, clienteGateway, catalogoProdutosLocal, clock);
    }

    @Bean
    public PedidoUseCases pedidoController(CarrinhoGateway carrinhoGateway,
                                           PedidoGateway pedidoGateway,
                                           ClienteGateway clienteGateway,
                                           CatalogoProdutosLocal catalogoProdutosLocal,
                                           PagamentoService pagamentoService,
                                           Clock clock,
                                           PainelPedidos painelPedidos) {
        return new PedidoUseCases(pedidoGateway, carrinhoGateway, clienteGateway,
                catalogoProdutosLocal, pagamentoService, clock, painelPedidos);
    }
}
