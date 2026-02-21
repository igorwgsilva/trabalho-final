# Sistema de Delivery - Clean Architecture

Este projeto é a avaliação final da disciplina de **Projeto de Sistemas de Software (2025-2)** da Universidade Federal do Espírito Santo (UFES). Consiste em um sistema de delivery desenvolvido para demonstrar a aplicação prática da Arquitetura Limpa (Clean Architecture).

## Equipe
- Igor Wendling Gurgel Silva
- Erik Satlher
- Bernardo Mangaraviti Carrerette
- Lucas Herbest Lopes
- João Vitor Henrique da Silva Abreu
- Andre Tavares Louzada
- Yuri Sousa Almeida

## Tecnologias Utilizadas
* **Linguagem:** Java 20
* **Gerenciador de Dependências:** Apache Maven
* **Banco de Dados:** SQLite
* **Interface Gráfica:** Java Swing

## Arquitetura e Padrões
O projeto foi estruturado utilizando os princípios da **Clean Architecture** para garantir o isolamento das regras de negócio e facilitar a manutenção. Ao longo da implementação, aplicamos princípios **S.O.L.I.D.** e diversos Padrões de Projeto para resolver problemas específicos do domínio, incluindo:
* **Builder:** Para a construção de pedidos complexos e montagem de pizzas (`PedidoBuilder`, `PizzaBuilder`).
* **State:** Para gerenciar o ciclo de vida e a transição de status dos pedidos (`IPedidoState`).
* **Decorator:** Para a adição dinâmica de ingredientes extras nas pizzas (`IngredienteDecorator`).
* **Factory / Abstract Factory:** Para a criação e instanciação flexível de diferentes tipos de pizzas (`IPizzaFactory`).
* **Singleton:** Para garantir uma única instância da ligação à base de dados SQLite (`ConexaoSingleton`).
* **Command:** Para o encapsulamento das ações e desacoplamento da navegação entre os ecrãs da interface gráfica (`AbrirPedidosCommand`, `AbrirClienteCommand`, `INavegacaoCommand`).
