/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalho.delivery.presentation.services;

//import com.mycompany.trabalho.delivery.aplicacao.useCases.AvancarEstadoPedidoUseCase;
//import com.mycompany.trabalho.delivery.aplicacao.useCases.BuscarPedidosPorCpfUseCase;
//import com.mycompany.trabalho.delivery.aplicacao.useCases.BuscarTodosOsClientesUseCase;
//import com.mycompany.trabalho.delivery.aplicacao.useCases.CadastrarClienteUseCase;
//import com.mycompany.trabalho.delivery.aplicacao.useCases.CancelarPedidoUseCase;
//import com.mycompany.trabalho.delivery.aplicacao.useCases.CriarPedidoUseCase;
//import com.mycompany.trabalho.delivery.aplicacao.useCases.DeletarClienteUseCase;
//import com.mycompany.trabalho.delivery.dominio.adapter.ConsoleLogAdapter;
//import com.mycompany.trabalho.delivery.dominio.port.IClienteRepository;
//import com.mycompany.trabalho.delivery.dominio.port.ILogService;
//import com.mycompany.trabalho.delivery.dominio.port.IPedidoRepository;
//import com.mycompany.trabalho.delivery.infraestrutura.logger.CSVMetodo;
//import com.mycompany.trabalho.delivery.infraestrutura.logger.Logger;
//import com.mycompany.trabalho.delivery.infraestrutura.repositories.ClienteRepositorySQLite;
//import com.mycompany.trabalho.delivery.infraestrutura.repositories.PedidoRepositorySQLite;
//import com.mycompany.trabalho.delivery.presentation.Presenter.ClientePresenter;
//import com.mycompany.trabalho.delivery.presentation.Presenter.PedidosPresenter;
//import com.mycompany.trabalho.delivery.presentation.controllers.ClienteController;
//import com.mycompany.trabalho.delivery.presentation.controllers.PedidosController;
//import com.mycompany.trabalho.delivery.presentation.ui.ClienteView;
//import com.mycompany.trabalho.delivery.presentation.ui.PedidosView;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//import javax.swing.JFrame;



import com.mycompany.trabalho.delivery.dominio.model.pizza.IPizzaFactory;
import com.mycompany.trabalho.delivery.dominio.port.IClienteRepository;
import com.mycompany.trabalho.delivery.dominio.port.ILogService;
import com.mycompany.trabalho.delivery.dominio.port.IPedidoRepository;
import com.mycompany.trabalho.delivery.dominio.port.IProvedorDePrecos;
import javax.swing.JFrame;

/**
 *
 * @author erko
 */
public class NavegadorDeViews {

    private final IClienteRepository clienteRepo;
    private final IPedidoRepository pedidoRepo;
    private final ILogService logService;
    private final IProvedorDePrecos provedor;
    private final IPizzaFactory pizzaFactory;
    

//    public NavegadorDeViews(IClienteRepository clienteRepo, IPedidoRepository pedidoRepo, ILogService logService) {
//       this.clienteRepo =  clienteRepo;
//        this.pedidoRepo = pedidoRepo;
//        this.logService = logService;
//    }
    
    public NavegadorDeViews(IClienteRepository clienteRepo, IPedidoRepository pedidoRepo, ILogService logService, IProvedorDePrecos provedor, IPizzaFactory pizzaFactory) {
       this.clienteRepo =  clienteRepo;
        this.pedidoRepo = pedidoRepo;
        this.logService = logService;
        this.provedor = provedor;
        this.pizzaFactory = pizzaFactory;
    }

//    public NavegadorDeViews() {
//        this.clienteRepo =  new ClienteRepositorySQLite();
//        this.pedidoRepo = new PedidoRepositorySQLite();
//        
//        //TODO verificar se é errado, deixando assim para fins de testes por enquanto. remover depois se for problemático
//        Logger.getInstancia().configurar(new CSVMetodo(';'));
//
//        ILogService logger = new ConsoleLogAdapter(); 
//        
//        this.logService = logger ;
//        
//    }
    
    //delega abertura de tela Clientes para AbrirClienteCommand
    public void abrirClienteView(){
        INavegacaoCommand command = new AbrirClienteCommand(clienteRepo, logService, this);
        command.executar();
    }
    
    //delega abertura de tela abrirItensPedidoView para o padrão AbrirItensPedidoCommand
    public void abrirItensPedidoView(JFrame parent, String cpf) {
        INavegacaoCommand command = new AbrirItensPedidoCommand(pedidoRepo, this, clienteRepo, parent, cpf, provedor, pizzaFactory);
        command.executar();
    }
    
    
    //delega abertura de PedidosView para  AbrirPedidosCommand
   public void abrirPedidosView(JFrame parent, String cpf) {
        INavegacaoCommand command = new AbrirPedidosCommand(pedidoRepo, clienteRepo, pizzaFactory, logService, this, parent, cpf);
        command.executar();
    }
    
    
    
}
