/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.trabalho.delivery.presentation.ui;

import com.mycompany.trabalho.delivery.aplicacao.dto.CreateClienteInputDTO;
import com.mycompany.trabalho.delivery.aplicacao.dto.CreateClienteOutputDTO;
import com.mycompany.trabalho.delivery.presentation.Presenter.ClientePresenter;
import com.mycompany.trabalho.delivery.presentation.controllers.ClienteController;
import com.mycompany.trabalho.delivery.presentation.services.NavegadorDeViews;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erko
 */
public class ClienteView extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ClienteView.class.getName());

    private ClienteController controller;
    private ClientePresenter presenter;
    private NavegadorDeViews navegadorDeViews;

    public ClienteView(ClienteController controller, ClientePresenter presenter, NavegadorDeViews navegadorDeViews) {
        this.controller = controller;
        this.presenter = presenter;
        this.navegadorDeViews = navegadorDeViews;
        initComponents();
        inicializar();
        setLocationRelativeTo(null);
    }

    public ClienteView() {//TODO verificar
        initComponents();
        setLocationRelativeTo(null);
    }

    public void inicializar() { //TODO verificar se esse metodo pode ser removido. aparentemente não 
        setTitle("Gestão de Clientes - Sistema Delivery");

        limparCampos();
        this.configurarListeners();
        atualizarTabela();
        
        this.setVisible(true);
    }
    
    
    //
    public void setGerenciadorDeViews(NavegadorDeViews gerenciadorDeViews){
        this.navegadorDeViews = gerenciadorDeViews;
    }

    public String getNome() {
        return txtNome.getText();
    }

    public String getEmail() {
        return txtEMail.getText();
    }

    public String getCidade() {
        return txtCidade.getText();
    }

    public String getBairro() {
        return txtBairro.getText();
    }

    public String getRua() {
        return txtRua.getText();
    }

    public String getNumero() {
        return txtNumero.getText();
    }

    public String getCpf() {
        return txtCPF.getText();
    }

    public int getLinhaSelecionada() {
        return tblClientes.getSelectedRow();
    }

    public void mostrarMensagem(String msg) {
        //mostra um optionpane com a mensagem
        javax.swing.JOptionPane.showMessageDialog(this, msg);
    }

    public void setController(ClienteController controller) {
        this.controller = controller;
    }

    public void adicionarClienteNaTabela(String cpf, String nome, String endereco, String email) {
        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();

        model.addRow(new Object[]{cpf, nome, endereco, email});
    }
   
  //configuração de listeners, aqui que são chamados os metodos com clique nos botões, por ex
    private void configurarListeners() {
        // botão Salvar
        btnSalvar.addActionListener((ActionEvent e) -> {

            salvarCliente();

        });

        // botão Limpar
        btnLimpar.addActionListener(e -> {
            limparCampos();
            this.mostrarMensagem("Limpando campos de entrada");
        });

        

        //botão Pedidos cliente===================================================================================
        btnPedidosDoCliente.addActionListener((ActionEvent e) -> {        

            
            String cpfSelecionado = getCpfSelecionado();

            if (cpfSelecionado != null) {
                
                navegadorDeViews.abrirPedidosView(this, cpfSelecionado);
//                abrirPedidosView(cpfSelecionado); // alterado para classe de serviço gerenciadora de views acima fazezr isso (padrão command)
            } else {
                mostrarMensagem("Por favor, selecione um cliente na tabela para ver os pedidos.");
            }

        });
        //botão Pedidos cliente=====================================================================
        
        // Ação do Botão Excluir
        btnExcluir.addActionListener(e -> {
            int row = tblClientes.getSelectedRow();
            String cpf = getCpfSelecionado();

            if (row != -1 && cpf != null) {

                controller.deletarCliente(cpf);

                // Remove a linha selecionada do modelo visual da JTable
                excluirLinhaDaTabela(row);

                mostrarMensagem("Cliente com CPF " + cpf + " excluído da visualização.");
            } else {
                mostrarMensagem("Selecione um cliente na tabela primeiro.");
            }
        });
    }

    private String getCpfSelecionado() {
        int index = tblClientes.getSelectedRow();
        if (index != -1) {
            // Assume que o CPF está na primeira coluna (índice 0) conforme seu modelo de tabela
            Object valor = tblClientes.getValueAt(index, 0);
            return (valor != null) ? valor.toString() : null;
        }
        return null;
    }

    private void excluirLinhaDaTabela(int row) {
        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
        model.removeRow(row);
    }

    private void salvarCliente(/*java.awt.event.ActionEvent evt*/) {
        CreateClienteInputDTO dto = new CreateClienteInputDTO();

        dto.setNome(txtNome.getText());
        dto.setEmail(txtEMail.getText());
        dto.setCpf(txtCPF.getText());

        dto.setCidade(txtCidade.getText());
        dto.setBairro(txtBairro.getText());
        dto.setRua(txtRua.getText());
        dto.setNumero(txtNumero.getText());

        if (controller != null) {
            Optional<CreateClienteOutputDTO> retorno = controller.salvar(dto);
            if (retorno.isPresent()) {
                CreateClienteOutputDTO output = retorno.get();
                String enderecoCompleto = dto.getRua() + ", " + dto.getNumero() + " - " + dto.getBairro() + " - " + dto.getCidade();

                adicionarClienteNaTabela(output.getCpf(), output.getNome(), enderecoCompleto, output.getEmail());

                System.out.println("DTO Enviado: " + dto.getNome() + " - CPF: " + dto.getCpf());
                mostrarMensagem("Salvo com sucesso!");

//                limparCampos();
            } else {
                mostrarMensagem("Erro ao salvar cliente.");
            }

        } else {
            System.err.println("Erro: Controller não foi inicializado!");
        }

    }

//       @Override
    public void atualizarTabela(/*List<CreateClienteOutputDTO> clientes*/) { //limpa tabela e preenche com clientes 
        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
        model.setRowCount(0);//limpa tabela
        List<CreateClienteOutputDTO> clientes = presenter.listarTodos();
        for (CreateClienteOutputDTO c : clientes) { //preenche com os dados da lista de clientes
            model.addRow(new Object[]{c.getCpf(), c.getNome(), c.getEnderecoFormatado(), c.getEmail()});

        }
    }

    public void limparCampos() {
        txtNome.setText("");
        txtEMail.setText("");
        txtCidade.setText("");
        txtBairro.setText("");
        txtRua.setText("");
        txtNumero.setText("");
        txtCPF.setText("");
    }

    public void limparTabela() {
        DefaultTableModel model = (DefaultTableModel) tblClientes.getModel();
        model.setRowCount(0);
    }

//    private void abrirPedidosView(String cpf) { //removendo para delegar para classe de serviços view, ela se encarrega de navegar entre as views pelo principio de inversão de dependencia
//        try {
//
//          
//
////            PedidosView pedidosView = new PedidosView();
////        (this, cpf);
//
//            //desativa a tela de cliente para evitar múltiplas instâncias e inconsistência
//            this.setEnabled(false);
//
////            pedidosView.iniciarView();
//
//            System.out.println("[Navegação] ClienteView suspensa. PedidosView aberta para o CPF: " + cpf);
//
//        } catch (Exception ex) {
//            logger.severe("Erro ao abrir PedidosView: " + ex.getMessage());
//            mostrarMensagem("Não foi possível carregar a tela de pedidos.");
//        }
//    }

//====================================================================
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblNome = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        lblEMail = new javax.swing.JLabel();
        txtEMail = new javax.swing.JTextField();
        lblEndereco = new javax.swing.JLabel();
        btnSalvar = new javax.swing.JButton();
        btnLimpar = new javax.swing.JButton();
        btnExcluir = new javax.swing.JButton();
        scrClientes = new javax.swing.JScrollPane();
        tblClientes = new javax.swing.JTable();
        lblCidade = new javax.swing.JLabel();
        txtCidade = new javax.swing.JTextField();
        lblBairro = new javax.swing.JLabel();
        txtBairro = new javax.swing.JTextField();
        lblRua = new javax.swing.JLabel();
        txtRua = new javax.swing.JTextField();
        lblNumero = new javax.swing.JLabel();
        txtNumero = new javax.swing.JTextField();
        btnPedidosDoCliente = new javax.swing.JButton();
        lblCPF = new javax.swing.JLabel();
        txtCPF = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblNome.setText("Nome");

        lblEMail.setText("E-mail");

        lblEndereco.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        lblEndereco.setText("Endereco: ");

        btnSalvar.setText("Salvar");

        btnLimpar.setText("Limpar");

        btnExcluir.setText("Excluir");

        tblClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "CPF", "Nome", "Endereço", "E-mail"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblClientes.setColumnSelectionAllowed(true);
        scrClientes.setViewportView(tblClientes);
        tblClientes.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);

        lblCidade.setText("Cidade");

        lblBairro.setText("Bairro");

        lblRua.setText("Rua");

        lblNumero.setText("Número");

        btnPedidosDoCliente.setText("Pedidos do Cliente");
        btnPedidosDoCliente.addActionListener(this::btnPedidosDoClienteActionPerformed);

        lblCPF.setText("CPF");

        txtCPF.addActionListener(this::txtCPFActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNome)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblNome)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEMail)
                            .addComponent(txtEMail, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(btnSalvar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnLimpar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnExcluir))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(scrClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 600, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(btnPedidosDoCliente)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCidade)
                            .addComponent(lblRua)
                            .addComponent(lblNumero)
                            .addComponent(lblEndereco)
                            .addComponent(lblCPF))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtRua)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(txtCidade)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblBairro)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, 269, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNome)
                    .addComponent(lblEMail))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEMail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCPF)
                    .addComponent(txtCPF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addComponent(lblEndereco)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCidade)
                    .addComponent(txtCidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBairro)
                    .addComponent(txtBairro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRua)
                    .addComponent(txtRua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNumero)
                    .addComponent(txtNumero, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSalvar)
                    .addComponent(btnLimpar)
                    .addComponent(btnExcluir))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPedidosDoCliente)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnPedidosDoClienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedidosDoClienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnPedidosDoClienteActionPerformed

    private void txtCPFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCPFActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCPFActionPerformed

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
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new ClienteView().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnExcluir;
    private javax.swing.JButton btnLimpar;
    private javax.swing.JButton btnPedidosDoCliente;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JLabel lblBairro;
    private javax.swing.JLabel lblCPF;
    private javax.swing.JLabel lblCidade;
    private javax.swing.JLabel lblEMail;
    private javax.swing.JLabel lblEndereco;
    private javax.swing.JLabel lblNome;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblRua;
    private javax.swing.JScrollPane scrClientes;
    private javax.swing.JTable tblClientes;
    private javax.swing.JTextField txtBairro;
    private javax.swing.JTextField txtCPF;
    private javax.swing.JTextField txtCidade;
    private javax.swing.JTextField txtEMail;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtNumero;
    private javax.swing.JTextField txtRua;
    // End of variables declaration//GEN-END:variables
}
