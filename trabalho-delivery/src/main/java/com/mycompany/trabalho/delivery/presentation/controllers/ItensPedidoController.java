/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalho.delivery.presentation.controllers;

import com.mycompany.trabalho.delivery.aplicacao.dto.ItemPedidoBebidaInputDTO;
import com.mycompany.trabalho.delivery.aplicacao.dto.ItemPedidoPizzaInputDTO;
import com.mycompany.trabalho.delivery.aplicacao.dto.ItemPreviewDTO;
import com.mycompany.trabalho.delivery.aplicacao.useCases.ICriarPedidoUseCase;
import com.mycompany.trabalho.delivery.aplicacao.useCases.ISimularPizzaUseCase;
import java.util.List;

/**
 *
 * @author André
 */
public class ItensPedidoController {
    private final ICriarPedidoUseCase criarPedido;
    private final ISimularPizzaUseCase simularPizza; // NOVO

    public ItensPedidoController(ICriarPedidoUseCase criarPedido, ISimularPizzaUseCase simularPizza) {
        this.criarPedido = criarPedido;
        this.simularPizza = simularPizza;
    }
    
    public void salvar(String cpf, List<ItemPedidoPizzaInputDTO> pizzas, List<ItemPedidoBebidaInputDTO> bebidas) {
        criarPedido.executar(cpf, pizzas, bebidas);
    }

    public ItemPreviewDTO simularPizza(String sabor, List<String> adicionais) {
        return simularPizza.executar(sabor, adicionais);
    }
}