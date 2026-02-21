package com.mycompany.trabalho.delivery.presentation.ui;



import com.mycompany.trabalho.delivery.aplicacao.dto.PedidoOutputDTO;
import com.mycompany.trabalho.delivery.presentation.controllers.PedidosController;
import com.mycompany.trabalho.delivery.presentation.Presenter.PedidosPresenter;
import com.mycompany.trabalho.delivery.presentation.services.NavegadorDeViews;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erko
 */
public class PedidosView extends javax.swing.JFrame {
    private JFrame parent;
//    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(PedidosView.class.getName());
    private PedidosController controller;
    private PedidosPresenter presenter;
    private String cpf;
    
    private NavegadorDeViews navegadorDeViews;

    
    
    
    public PedidosView() {
        this.parent=null;
        
        initComponents();
        
        
    }

    public PedidosView(JFrame parent, String cpf, PedidosController controller, PedidosPresenter presenter) {
        this.parent=parent;
        this.cpf= cpf;
        this.controller = controller;
        this.presenter = presenter;
        initComponents();
        iniciarView();
    }
    
 
 
    
    public void iniciarView(){
//        limparTabelas();//TODO com problema esse metodo
        this.setLocationRelativeTo(parent);
        configurarListeners();
        setLblCpfCliente(this.cpf); //coloca nome do cliente na lbl da tela
        atualizarTabela();
        setVisible(true);  
        
    }
    
    public void limparTabelas(){
        DefaultTableModel modelTblPedidos = (DefaultTableModel) tblPedidos.getModel(); //limpa tabela
        modelTblPedidos.setRowCount(0);
        
//        DefaultTableModel model = (DefaultTableModel) tblPedidos.getModel();
//        model.setRowCount(0); 
    }
    
    public void atualizarTabela() { //limpa tabela e preenche com clientes 
        
        DefaultTableModel model = (DefaultTableModel) tblPedidos.getModel();
        model.setRowCount(0);//limpa tabela
        presenter.mostrarPedidos(cpf);
        
        List<PedidoOutputDTO> pedidos = presenter.mostrarPedidos(cpf);
        for (PedidoOutputDTO c : pedidos) { //preenche com os dados da lista de pedidos
            model.addRow(new Object[]{c.getId(), c.getValorTotal(), c.getEstado()});

        }
    }
    
    
    private void configurarListeners() {
        
        //novo pedido (abre a ItensPedidoView)
        btnNovoPedido.addActionListener(e -> {
            if (this.navegadorDeViews != null) {
                // Invoca o navegador repassando a própria view como parent e o CPF do contexto
                this.navegadorDeViews.abrirItensPedidoView(this, this.cpf);
            } else {
                throw new IllegalStateException("Dependência NavegadorDeViews não foi injetada.");
            }
        });

        //cancelar o pedido selecionado
        btnCancelarPedido.addActionListener((ActionEvent e) -> {
            int row = tblPedidos.getSelectedRow();
            int idPedidoDaLinha = getIdPedidoDalinha(row);
            if (row != -1) { //se row == -1 então não tem nenhum pedido na tabela
                mostrarMensagem("Pedido cancelado com sucesso.");
                int id = getPedidoSelecionado();
                controller.cancelarPedido(id);
            } else {
                mostrarMensagem("Selecione um pedido para cancelar.");
            }
        });

        //visualizar detalhes do pedido
        btnVerPedido.addActionListener((ActionEvent e) -> {
            int row = tblPedidos.getSelectedRow();
            if (row != -1) { //se row =1 então não tem nenhum pedido na tabela
                verPedido(row);
            } else {
                mostrarMensagem("Selecione um pedido para visualizar.");
            }
        });

        // Ação para avançar o estado do pedido (ex: De 'Preparando' para 'Em Entrega')
        btnAvancarEstado.addActionListener((ActionEvent e) -> {
            int row = tblPedidos.getSelectedRow();
            if (row != -1) {
                mostrarMensagem("Estado avançado com Sucesso.");
                int id = getPedidoSelecionado();
                controller.avancarEstado(id);
            } else {
                mostrarMensagem("Selecione um pedido para avançar o status.");
                
            }
        });
//        listener para detectar quando esta janela fechar
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (parent != null) {
                    parent.setEnabled(true); //reativa a ClienteView
                    parent.toFront();        //traz pra frente
                }
            }
        }); 
        
    }
    
    private void novoPedido() {
        System.out.println("criando novo pedido.");
        
//        controller.criarPedido(this.cpf); TODO 
//        ItensPedidoView itensView = new ItensPedidoView();
//        itensView.iniciarView();
    }

    private void cancelarPedido(int row) {
        int confirm = JOptionPane.showConfirmDialog(this, "Deseja cancelar esse pedido??", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            
//             controller.cancelarPedido(idPedido);
            mostrarMensagem("Pedido cancelado com sucesso.");
        }
    }

    private void verPedido(int row) {
        mostrarMensagem("Visualizando detalhes do pedido selecionado.");
    }

    private void avancarEstado(int row) {
        mostrarMensagem("Status do pedido avançado.");
    }

    
    
    public void mostrarMensagem(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem);
    }
    
    private int getIdPedidoDalinha(int linha) {
         //verifica se o índice da linha é válido
        if (linha == -1) {
            return -1;
        }

        //obtem o objeto da primeira coluna (índice 0) que contém o ID
        Object valorDaCelula = tblPedidos.getValueAt(linha, 0);
        //verifica se a celula está vazia
        if (valorDaCelula == null) {
            return -1;
        }


        return Integer.parseInt(valorDaCelula.toString());
    }
    
    
    private void setLblCpfCliente(String cpf) {
        lblNomeCliente.setText("Pedidos do cliente de CPF: " + cpf);
    }
    private void completarTabela(){
        
    }
    
    private void preencherTabelaPedidos() {
        if (presenter == null || cpf == null) {
            return;
        }

        
        limparTabelas();//limpa a tabela antes de preencher
        
        DefaultTableModel model = getTblModel();

        //chamada ao método da presenter
        List<PedidoOutputDTO> pedidos = presenter.mostrarPedidos(cpf);

        if (pedidos != null) {
            for (PedidoOutputDTO pedido : pedidos) {
                model.addRow(new Object[]{
                    pedido.getId(),       // Coluna 0: ID Pedido
                    pedido.getValorTotal(),     // Coluna 1: Data
                    pedido.getEstado(),   // Coluna 2: Status
                         
                });
            }
        }
    }
    
    
    private DefaultTableModel getTblModel(){
         return (DefaultTableModel) tblPedidos.getModel();
    }
    
    
    public void setNavegadorDeViews(NavegadorDeViews gerenciadorDeViews) {
        this.navegadorDeViews =gerenciadorDeViews;
    }
 
    
    private int getPedidoSelecionado() {
    int index = tblPedidos.getSelectedRow();
    
    if (index != -1) {
        Object valor = tblPedidos.getValueAt(index, 0);
        if (valor != null) {
            return (int) valor; 
        }
    }
    // Retorna -1 para indicar que nenhum pedido foi encontrado/selecionado
    return -1; 
}
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNomeCliente = new javax.swing.JLabel();
        scrPedidos = new javax.swing.JScrollPane();
        tblPedidos = new javax.swing.JTable();
        btnNovoPedido = new javax.swing.JButton();
        btnCancelarPedido = new javax.swing.JButton();
        btnVerPedido = new javax.swing.JButton();
        btnAvancarEstado = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblNomeCliente.setText("Pedido do cliente de CPF: 123");

        tblPedidos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "Valor Total", "Status"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrPedidos.setViewportView(tblPedidos);

        btnNovoPedido.setText("Novo Pedido");

        btnCancelarPedido.setText("Cancelar Pedido");

        btnVerPedido.setText("Ver Pedido");
        btnVerPedido.addActionListener(this::btnVerPedidoActionPerformed);

        btnAvancarEstado.setText("Avançar Estado");
        btnAvancarEstado.addActionListener(this::btnAvancarEstadoActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scrPedidos)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNomeCliente)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnNovoPedido)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnVerPedido)
                                .addGap(59, 59, 59)
                                .addComponent(btnAvancarEstado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancelarPedido)))
                        .addGap(0, 3, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblNomeCliente)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrPedidos, javax.swing.GroupLayout.PREFERRED_SIZE, 392, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnNovoPedido)
                        .addComponent(btnVerPedido))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancelarPedido)
                        .addComponent(btnAvancarEstado)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAvancarEstadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAvancarEstadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnAvancarEstadoActionPerformed

    private void btnVerPedidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerPedidoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnVerPedidoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
//            logger.log(java.util.logging.Level.SEVERE, null, ex);//TODO comentei pois tinha erro verificar se é por motivo de não ter log implementado nessa classe
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new PedidosView().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAvancarEstado;
    private javax.swing.JButton btnCancelarPedido;
    private javax.swing.JButton btnNovoPedido;
    private javax.swing.JButton btnVerPedido;
    private javax.swing.JLabel lblNomeCliente;
    private javax.swing.JScrollPane scrPedidos;
    private javax.swing.JTable tblPedidos;
    // End of variables declaration//GEN-END:variables

  

}
