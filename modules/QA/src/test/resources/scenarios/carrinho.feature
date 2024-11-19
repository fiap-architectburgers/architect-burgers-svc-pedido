# language: pt
Funcionalidade: Carrinho de compras

  Cenário: Iniciar um novo carrinho de compras para cliente não cadastrado
    Dado que um cliente está iniciando sua compra
    Quando o cliente começa um novo pedido informando seu nome como identificação
    Então a resposta é um carrinho de compras
    E o carrinho está vazio
    E o carrinho contém o nome do cliente

  # WIP
#  Cenário: Incluir item no carrinho de compras
#    Dado que um cliente possui um carrinho de compras
#    Quando o cliente adiciona um item do cardápio
#    Então a resposta é um carrinho de compras
#    E o carrinho contém em seus itens o produto selecionado
#

