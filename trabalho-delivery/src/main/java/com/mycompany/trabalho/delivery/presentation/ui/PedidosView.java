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
        this.setLocationRelativeTo(parent);
        configurarListeners();
        setLblCpfCliente(this.cpf); 
        atualizarTabela(); 
        setVisible(true);
    }
    
    public void atualizarTabela() {
        DefaultTableModel model = (DefaultTableModel) tblPedidos.getModel();
        model.setRowCount(0); // Limpa a tabela
        
        List<PedidoOutputDTO> pedidos = presenter.mostrarPedidos(cpf);
        if (pedidos != null) {
            for (PedidoOutputDTO pedido : pedidos) {
                String statusDescricao = "";
                if (pedido.getEstado() != null) {
                    statusDescricao = pedido.getEstado().getDescricao();
                }
                
                model.addRow(new Object[]{
                    pedido.getId(),
                    pedido.getValorTotal(),
                    statusDescricao 
                });
            }
        }
    }
    
    
   private void configurarListeners() {
       
        btnNovoPedido.addActionListener(e -> {
            if (this.navegadorDeViews != null) {
                this.navegadorDeViews.abrirItensPedidoView(this, this.cpf);
            } else {
                throw new IllegalStateException("Dependência NavegadorDeViews não foi injetada.");
            }
        });

        btnCancelarPedido.addActionListener((ActionEvent e) -> {
            int row = tblPedidos.getSelectedRow();
            if (row != -1) { 
                int id = getPedidoSelecionado();
                controller.cancelarPedido(id);
                atualizarTabela(); // Atualiza a tabela após a mudança
                mostrarMensagem("Pedido cancelado com sucesso.");
            } else {
                mostrarMensagem("Selecione um pedido para cancelar.");
            }
        });

        btnVerPedido.addActionListener((ActionEvent e) -> {
            int row = tblPedidos.getSelectedRow();
            if (row != -1) { 
                verPedido(row);
            } else {
                mostrarMensagem("Selecione um pedido para visualizar.");
            }
        });

        btnAvancarEstado.addActionListener((ActionEvent e) -> {
            int row = tblPedidos.getSelectedRow();
            if (row != -1) {
                int id = getPedidoSelecionado();
                controller.avancarEstado(id); // Muda no banco
                atualizarTabela();           
                mostrarMensagem("Estado avançado com Sucesso.");
            } else {
                mostrarMensagem("Selecione um pedido para avançar o status.");
            }
        });

       
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if (parent != null) {
                    parent.setEnabled(true); 
                    parent.toFront();        
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
            try {
                // Abordagem Segura: Number abrange Integer, Long, etc.
                if (valor instanceof Number number) {
                    return number.intValue();
                }

                // Fallback: Caso o valor esteja como String na tabela
                return Integer.parseInt(valor.toString());
            } catch (NumberFormatException e) {
                // Log de erro técnico (evitar alert() em camadas de lógica)
                System.err.println("Erro de conversão: Valor da tabela não é um inteiro válido.");
            }
        }
    }
    return -1; 
}
    public void limparTabelas(){
        DefaultTableModel modelTblPedidos = (DefaultTableModel) tblPedidos.getModel(); //limpa tabela
        modelTblPedidos.setRowCount(0);
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
