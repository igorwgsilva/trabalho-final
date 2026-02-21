package com.mycompany.trabalho.delivery.aplicacao.useCases;

import com.mycompany.trabalho.delivery.aplicacao.dto.ItemPreviewDTO;
import com.mycompany.trabalho.delivery.dominio.model.pizza.IPizzaFactory;
import com.mycompany.trabalho.delivery.dominio.model.pizza.Ingrediente;
import com.mycompany.trabalho.delivery.dominio.model.pizza.PizzaComponente;
import com.mycompany.trabalho.delivery.dominio.port.IProvedorDePrecos;
import java.util.List;

public class SimularPizzaUseCase implements ISimularPizzaUseCase {
    private final IPizzaFactory pizzaFactory;
    private final IProvedorDePrecos provedorPrecos;

    public SimularPizzaUseCase(IPizzaFactory pizzaFactory, IProvedorDePrecos provedorPrecos) {
        this.pizzaFactory = pizzaFactory;
        this.provedorPrecos = provedorPrecos;
    }

    @Override
    public ItemPreviewDTO executar(String sabor, List<String> adicionais) {
        PizzaComponente pizza = pizzaFactory.criarPizza(sabor);
        
        if (adicionais != null) {
            for (String adicional : adicionais) {
                double precoAdicional = provedorPrecos.buscaPreco(adicional);
                pizza = new Ingrediente(pizza, adicional, precoAdicional); 
            }
        }
        
        return new ItemPreviewDTO(pizza.getDescricao(), pizza.getPreco());
    }
}