package com.mycompany.trabalho.delivery.aplicacao.useCases;

import com.mycompany.trabalho.delivery.aplicacao.dto.ItemPedidoBebidaInputDTO;
import com.mycompany.trabalho.delivery.aplicacao.dto.ItemPedidoPizzaInputDTO;
import com.mycompany.trabalho.delivery.dominio.model.bebida.Bebida;
import com.mycompany.trabalho.delivery.dominio.model.cliente.Cliente;
import com.mycompany.trabalho.delivery.dominio.model.pedido.Item;
import com.mycompany.trabalho.delivery.dominio.model.pedido.Pedido;
import com.mycompany.trabalho.delivery.dominio.model.pedido.PedidoPendenteState;
import com.mycompany.trabalho.delivery.dominio.model.pizza.IPizzaFactory;
import com.mycompany.trabalho.delivery.dominio.model.pizza.PizzaComponente;
import com.mycompany.trabalho.delivery.dominio.port.IClienteRepository;
import com.mycompany.trabalho.delivery.dominio.port.ILogService;
import com.mycompany.trabalho.delivery.dominio.port.IPedidoRepository;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author André
 */
public class CriarPedidoUseCase implements ICriarPedidoUseCase {
    private final IPedidoRepository pedidoRepository;
    private final IClienteRepository clienteRepository;
    private final IPizzaFactory pizzaFactory; 
    private final ILogService logger; 

   
    public CriarPedidoUseCase(IPedidoRepository pedidoRepository, IClienteRepository clienteRepository, IPizzaFactory pizzaFactory, ILogService logger) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.pizzaFactory = pizzaFactory;
        this.logger = logger;
    }

    public CriarPedidoUseCase(IPedidoRepository pedidoRepository, IClienteRepository clienteRepository, IPizzaFactory pizzaFactory) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.pizzaFactory = pizzaFactory;
        this.logger = null; 
    }
    
    public void executar(String cpf, List<ItemPedidoPizzaInputDTO> pizzas, List<ItemPedidoBebidaInputDTO> bebidas) {
      
        Cliente cliente = clienteRepository.buscarClientePorCPF(cpf);
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não encontrado para o CPF informado.");
        }
        
        List<Item> itensPedido = new ArrayList<>();

        if (pizzas != null) {
            for (ItemPedidoPizzaInputDTO itemPizza : pizzas) {
                PizzaComponente pizza = pizzaFactory.criarPizza(itemPizza.getSabor());
                itensPedido.add(pizza);
            }
        }

        if (bebidas != null) {
            for (ItemPedidoBebidaInputDTO itemBebida : bebidas) {
                Bebida bebida = new Bebida(itemBebida.getNome(), itemBebida.getPreco());
                itensPedido.add(bebida);
            }
        }

       Pedido novoPedido = new Pedido(cliente, itensPedido, new PedidoPendenteState()); 
        pedidoRepository.salvarPedido(novoPedido);
    }
}