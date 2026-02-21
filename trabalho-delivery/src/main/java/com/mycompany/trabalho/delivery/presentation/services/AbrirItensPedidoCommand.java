/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalho.delivery.presentation.services;

import com.mycompany.trabalho.delivery.aplicacao.useCases.CriarPedidoUseCase;
import com.mycompany.trabalho.delivery.aplicacao.useCases.ICriarPedidoUseCase;
import com.mycompany.trabalho.delivery.aplicacao.useCases.MontarPedidoUseCase;
import com.mycompany.trabalho.delivery.dominio.model.pizza.IPizzaFactory;
import com.mycompany.trabalho.delivery.dominio.port.IClienteRepository;
import com.mycompany.trabalho.delivery.dominio.port.IPedidoRepository;
import com.mycompany.trabalho.delivery.dominio.port.IProvedorDePrecos;
import com.mycompany.trabalho.delivery.presentation.Presenter.ItensPedidoPresenter;
import com.mycompany.trabalho.delivery.presentation.controllers.ItensPedidoController;
import com.mycompany.trabalho.delivery.presentation.ui.ItensPedidoView;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author erko
 */
public class AbrirItensPedidoCommand implements INavegacaoCommand {
    private final IPedidoRepository pedidoRepo;
    private final IClienteRepository clienteRepo;
    private final IPizzaFactory pizzaFactory;
    private final NavegadorDeViews navegador;
    private final JFrame parent;
    private final String cpf;
    private IProvedorDePrecos provedor;
    
    
    public AbrirItensPedidoCommand(IPedidoRepository pedidoRepo, NavegadorDeViews navegador, IClienteRepository clienteRepo,
                                   JFrame parent, String cpf, IProvedorDePrecos provedor, IPizzaFactory pizzaFactory) {
        this.pedidoRepo = pedidoRepo;
        this.navegador = navegador;
        this.parent = parent;
        this.clienteRepo = clienteRepo;
        this.pizzaFactory = pizzaFactory;
        this.cpf = cpf;
        this.provedor = provedor;
        
    }
    
    
//    public MontarPedidoUseCase(IPedidoRepository repositorioPedidos, IClienteRepository repositorioClientes, IProvedorDePrecos provedor, 

    
    @Override
    public void executar() {
        //instanciação do Use Case
        ICriarPedidoUseCase criarPedidoUC = new CriarPedidoUseCase(pedidoRepo, clienteRepo, pizzaFactory);
                

        //instanciação do Controller e Presenter
        ItensPedidoController controller = new ItensPedidoController(criarPedidoUC);
        ItensPedidoPresenter presenter = new ItensPedidoPresenter();

        // Injeção na View
        ItensPedidoView view = new ItensPedidoView(parent, cpf, controller, presenter, navegador); //TODO Arrumar 
//                parent, cpf, controller, presenter, navegador);

        // Lógica de UI (Bloqueio da tela anterior)
        parent.setEnabled(false);
        view.setLocationRelativeTo(parent);
        view.setVisible(true);
       

        
        //prepara para tela pai se tornar ativa ao fechar esta
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                parent.setEnabled(true);
                parent.toFront();
            }
        });
    }
}
