Architect Burgers - Microsserviço de Pedidos
=============================================

### Testes

Executar testes unitários

    mvn test

Executar testes unitários e de integração do Desenvolvedor, com validação de cobertura

    mvn verify

Executar testes de QA incluindo BDD

    mvn -P qa-tests verify

### Evidência de cobertura de teste:

- Screenshot do report no arquivo `docs/evidencia_cobertura.png`

ou

- Baixar artefato mais atualizado após execução do pipeline `verify-on-pr`, job "Upload coverage reports"


### How-TOs

Preparar DB local para desenvolvimento

Pré-requisitos: instância de PostgreSQL

Executar os seguintes comandos como administrador:

    create user burger_user_pedido with password 'burgerYeah';
    create database archburgers_svc_pedido owner burger_user_pedido;

