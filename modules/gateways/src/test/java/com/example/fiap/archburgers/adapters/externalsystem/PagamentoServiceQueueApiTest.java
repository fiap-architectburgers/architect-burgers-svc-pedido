package com.example.fiap.archburgers.adapters.externalsystem;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.env.Environment;

import static org.junit.jupiter.api.Assertions.*;

class PagamentoServiceQueueApiTest {

    @Test
    void whenConstructedWithValidEnvironment_expectNoException() {
        Environment environment = Mockito.mock(Environment.class);

        Mockito.when(environment.getProperty("archburgers.integration.sqs.sqsEndpoint")).thenReturn("validSqsEndpoint");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pedidosQueueName")).thenReturn("validPedidosQueueName");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pedidosQueueUrl")).thenReturn("validPedidosQueueUrl");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pagamentosConcluidosQueueName")).thenReturn("validPagamentosConcluidosQueueName");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pagamentosConcluidosQueueUrl")).thenReturn("validPagamentosConcluidosQueueUrl");

        assertDoesNotThrow(() -> new PagamentoServiceQueueApi(environment));
    }

    @Test
    void whenConstructedWithNoEndpoint_expectException() {
        Environment environment = Mockito.mock(Environment.class);

        Mockito.when(environment.getProperty("archburgers.integration.sqs.sqsEndpoint")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> new PagamentoServiceQueueApi(environment));
    }

    @Test
    void whenConstructedWithNoPedidosQueueName_expectException() {
        Environment environment = Mockito.mock(Environment.class);

        Mockito.when(environment.getProperty("archburgers.integration.sqs.sqsEndpoint")).thenReturn("validSqsEndpoint");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pedidosQueueName")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> new PagamentoServiceQueueApi(environment));
    }

    @Test
    void whenConstructedWithNoPedidosQueueUrl_expectException() {
        Environment environment = Mockito.mock(Environment.class);
        
        Mockito.when(environment.getProperty("archburgers.integration.sqs.sqsEndpoint")).thenReturn("validSqsEndpoint");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pedidosQueueName")).thenReturn("validPedidosQueueName");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pedidosQueueUrl")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> new PagamentoServiceQueueApi(environment));
    }

    @Test
    void whenConstructedWithNoPagamentosConcluidosQueueName_expectException() {
        Environment environment = Mockito.mock(Environment.class);

        Mockito.when(environment.getProperty("archburgers.integration.sqs.sqsEndpoint")).thenReturn("validSqsEndpoint");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pedidosQueueName")).thenReturn("validPedidosQueueName");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pedidosQueueUrl")).thenReturn("validPedidosQueueUrl");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pagamentosConcluidosQueueName")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> new PagamentoServiceQueueApi(environment));
    }
    
    @Test
    void whenConstructedWithNoPagamentosConcluidosQueueUrl_expectException() {
        Environment environment = Mockito.mock(Environment.class);

        Mockito.when(environment.getProperty("archburgers.integration.sqs.sqsEndpoint")).thenReturn("validSqsEndpoint");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pedidosQueueName")).thenReturn("validPedidosQueueName");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pedidosQueueUrl")).thenReturn("validPedidosQueueUrl");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pagamentosConcluidosQueueName")).thenReturn("validPagamentosConcluidosQueueName");
        Mockito.when(environment.getProperty("archburgers.integration.sqs.pagamentosConcluidosQueueUrl")).thenReturn(null);

        assertThrows(IllegalArgumentException.class, () -> new PagamentoServiceQueueApi(environment));
    }

}