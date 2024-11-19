# language: pt
Funcionalidade: Tratamento do Pedido

  Cenário: Validar um pedido Recebido
    Dado um cliente realizou e finalizou o pagamento de um pedido
    E este pedido está com status Recebido
    Quando o chefe de cozinha valida o pedido
    Então a resposta é um pedido com status Em Preparação

