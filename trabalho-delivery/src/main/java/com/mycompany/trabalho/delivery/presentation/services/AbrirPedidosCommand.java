/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalho.delivery.presentation.services;

import com.mycompany.trabalho.delivery.aplicacao.useCases.AvancarEstadoPedidoUseCase;
import com.mycompany.trabalho.delivery.aplicacao.useCases.BuscarPedidosPorCpfUseCase;
import com.mycompany.trabalho.delivery.aplicacao.useCases.CancelarPedidoUseCase;
import com.mycompany.trabalho.delivery.aplicacao.useCases.CriarPedidoUseCase;
import com.mycompany.trabalho.delivery.dominio.model.pizza.IPizzaFactory;
import com.mycompany.trabalho.delivery.dominio.port.IClienteRepository;
import com.mycompany.trabalho.delivery.dominio.port.ILogService;
import com.mycompany.trabalho.delivery.dominio.port.IPedidoRepository;
import com.mycompany.trabalho.delivery.presentation.Presenter.PedidosPresenter;
import com.mycompany.trabalho.delivery.presentation.controllers.PedidosController;
import com.mycompany.trabalho.delivery.presentation.ui.PedidosView;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;

/**
 *
 * @author erko
 */
public class AbrirPedidosCommand implements INavegacaoCommand{
    private final IPedidoRepository pedidoRepo;
    private final IClienteRepository clienteRepo;
    private final IPizzaFactory pizzaFactory;
    private final ILogService logService;
    private final NavegadorDeViews navegador;
    private final JFrame parent;
    private final String cpf;
    
    
    public AbrirPedidosCommand(
            IPedidoRepository pedidoRepo, 
            IClienteRepository clienteRepo,
            IPizzaFactory pizzaFactory,
            ILogService logService, 
            NavegadorDeViews navegador, 
            JFrame parent, 
            String cpf) {
        this.pedidoRepo = pedidoRepo;
        this.clienteRepo = clienteRepo;
        this.pizzaFactory = pizzaFactory;
        this.logService = logService;
        this.navegador = navegador;
        this.parent = parent;
        this.cpf = cpf;
    }
    
     @Override
    public void executar() {
        //casos de uso(camada de aplicação)
        CriarPedidoUseCase criarUC = new CriarPedidoUseCase(pedidoRepo, clienteRepo, /*logService,*/ pizzaFactory);//TODO IGOR adiconar LOG
        CancelarPedidoUseCase cancelarUC = new CancelarPedidoUseCase(pedidoRepo);
        AvancarEstadoPedidoUseCase avancarUC = new AvancarEstadoPedidoUseCase(pedidoRepo);
        BuscarPedidosPorCpfUseCase buscarUC = new BuscarPedidosPorCpfUseCase(pedidoRepo);

        //controller e presenter (camada presenter)
        PedidosController controller = new PedidosController(avancarUC, cancelarUC, criarUC);
        PedidosPresenter presenter = new PedidosPresenter(pedidoRepo, buscarUC);

        //injeção na view 
        PedidosView view = new PedidosView(parent, cpf, controller, presenter);
        view.setNavegadorDeViews(navegador);

        // 4. Lógica de Navegação (Bloqueio da tela anterior e transição)
        parent.setEnabled(false);
        view.setVisible(true);
        
        // Reativa a tela pai ao fechar a visualização de pedidos
        view.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                parent.setEnabled(true);
                parent.toFront();
            }
        });
    }
    
}
