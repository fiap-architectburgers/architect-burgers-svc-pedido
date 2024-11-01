Architect Burgers - Microsserviço de Pedidos
=============================================

### Testes

Executar testes unitários

    mvn test

Executar testes unitários e de integração do Desenvolvedor, com validação de cobertura

    mvn verify

Executar testes de QA incluindo BDD

    mvn -P qa-tests verify


### How-TOs

Preparar DB local para desenvolvimento

Pré-requisitos: instância de PostgreSQL

Executar os seguintes comandos como administrador:

    create user burger_user_pedido with password 'burgerYeah';
    create database archburgers_svc_pedido owner burger_user_pedido;

