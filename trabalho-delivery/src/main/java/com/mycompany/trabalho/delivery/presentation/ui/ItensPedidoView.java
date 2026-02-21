/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.trabalho.delivery.presentation.ui;

import com.mycompany.trabalho.delivery.aplicacao.dto.ItemPedidoBebidaInputDTO;
import com.mycompany.trabalho.delivery.aplicacao.dto.ItemPedidoPizzaInputDTO;
import com.mycompany.trabalho.delivery.dominio.model.pedido.Pedido;
import com.mycompany.trabalho.delivery.presentation.Presenter.ItensPedidoPresenter;
import com.mycompany.trabalho.delivery.presentation.controllers.ItensPedidoController;
import com.mycompany.trabalho.delivery.presentation.services.NavegadorDeViews;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author erko
 */
public class ItensPedidoView extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(ItensPedidoView.class.getName());

    private JFrame parent;
    private String cpf;
    private ItensPedidoController controller;
    private ItensPedidoPresenter presenter;
    private NavegadorDeViews navegadorDeViews;
    
    private List<ItemPedidoPizzaInputDTO> pizzasTemp = new ArrayList<>();
    private List<ItemPedidoBebidaInputDTO> bebidasTemp = new ArrayList<>();
    
    //para fins de teste
   

    // --- Novos Atributos de Estado Local ---
    private String idPedidoAtual; 
    private List<String> ingredientesExtrasAtuais;
    
    /**
     * Creates new form ItensView
     */
    public ItensPedidoView() {
        initComponents();
        iniciarView();
        
    }
    
     public ItensPedidoView(JFrame parent, String cpf, ItensPedidoController controller, ItensPedidoPresenter presenter, NavegadorDeViews navegadorDeViews) {
        this.parent = parent;
        this.cpf = cpf;
        this.controller = controller;
        this.presenter = presenter;
        this.navegadorDeViews = navegadorDeViews;
        
        // Inicializa o estado do novo pedido
//        this.idPedidoAtual = UUID.randomUUID().toString();
//        this.ingredientesExtrasAtuais = new ArrayList<>();

      
        initComponents();
        
        
       
        iniciarView();
        
        // Centraliza em relação à tela de Pedidos (parent)
        this.setLocationRelativeTo(parent);
    }

    public void iniciarView(){
        limparTodosCombos();
        limparTabelas();
        
        configurarListeners();
        this.setVisible(true);
    }
    
    
    
    public void limparTodosCombos() {
        
        cmbBebida.removeAllItems();
        cmbIngrediente.removeAllItems();
        cmbTamanhoBebida.removeAllItems();
    }
    
    public void adicionarOpcaoBebida(String nomeBebida) {
        cmbBebida.addItem(nomeBebida);
    }
    
    public void adicionarOpcaoIngrediente(String nomeIngrediente) {
        cmbIngrediente.addItem(nomeIngrediente);
    }
    
    public void adicionarOpcaoTamanhoBebida(String tamanho) {
        cmbTamanhoBebida.addItem(tamanho);
    }
    
    public void limparTabelas(){ //esvazia as tabelas
        DefaultTableModel modelPizza = (DefaultTableModel) tblAdicionaisPizza.getModel();
        modelPizza.setRowCount(0);
        DefaultTableModel modelItens = (DefaultTableModel) tblItens.getModel();
        modelItens.setRowCount(0);
    }
    
    public void adicionarItemNaTabela(Integer idItem, String descricao, Integer quantidade, Double valor) {
        DefaultTableModel model = (DefaultTableModel) tblItens.getModel();
        model.addRow(new Object[]{idItem,descricao,quantidade,valor});
    }
    
    public void mostrarMensagem(String msg) {
        //mostra um optionpane com a mensagem
        javax.swing.JOptionPane.showMessageDialog(this, msg);
    } 
    
    // Listeners dos botões
    private void configurarListeners() {

        btnAddPizzaAoPedido.addActionListener(e -> {
            String saborSelecionado = cmbListaPizzasBase.getSelectedItem().toString();

            // Cria o DTO
            ItemPedidoPizzaInputDTO pizzaDTO = new ItemPedidoPizzaInputDTO();
            pizzaDTO.setSabor(saborSelecionado);
            // (Se já tiver a lógica de adicionais, preencha a lista de adicionais do DTO aqui)

            // Salva na lista temporária e atualiza a tabela visual
            pizzasTemp.add(pizzaDTO);
            adicionarItemNaTabela(pizzasTemp.size(), "Pizza " + saborSelecionado, 1, 0.0); // O valor (0.0) pode ser ajustado depois com o provedor de preços
            atualizarTotalPedido();
        });
        
        btnAddBebidaPedido.addActionListener(e -> {
            String nomeBebida = cmbBebida.getSelectedItem().toString();

            ItemPedidoBebidaInputDTO bebidaDTO = new ItemPedidoBebidaInputDTO();
            bebidaDTO.setNome(nomeBebida);
            // bebidaDTO.setPreco(...); // Se você tiver o preço no combo ou em outro lugar, defina aqui

            bebidasTemp.add(bebidaDTO);
            adicionarItemNaTabela(bebidasTemp.size(), nomeBebida, 1, 0.0);
            atualizarTotalPedido();
        });
        
        
        btnFinalizarPedido.addActionListener(e -> {
            if (pizzasTemp.isEmpty() && bebidasTemp.isEmpty()) {
                mostrarMensagem("Adicione pelo menos um item ao pedido!");
                return;
            }

            try {
                // Envia tudo para o Controller!
                controller.salvar(this.cpf, pizzasTemp, bebidasTemp);

                mostrarMensagem("Pedido criado com sucesso!");
                this.dispose(); // Fecha a tela e volta para a anterior
            } catch (Exception ex) {
                mostrarMensagem("Erro ao criar pedido: " + ex.getMessage());
            }
        });
//       if (btnAddPizzaAoPedido != null) {
//            btnAddPizzaAoPedido.addActionListener(e -> {
//                // Exemplo de como irá capturar os dados da View para o Controller:
//                // String saborBase = (String) cmbListaPizzasBase.getSelectedItem();
//                // controller.adicionarPizzaAoPedido(saborBase, listaDeIngredientesSelecionados);
//                JOptionPane.showMessageDialog(this, "Pizza adicionada (Lógica a ser implementada no Controller).");
//            });
//        }
//
//        if (btnAddBebidaPedido != null) {
//            btnAddBebidaPedido.addActionListener(e -> {
//                // String bebida = (String) cmbBebida.getSelectedItem();
//                // String tamanho = (String) cmbTamanhoBebida.getSelectedItem();
//                // controller.adicionarBebidaAoPedido(bebida, tamanho);
//                JOptionPane.showMessageDialog(this, "Bebida adicionada (Lógica a ser implementada no Controller).");
//            });
//        }
//
//        if (btnIncluirAdiciona != null) {
//            btnIncluirAdiciona.addActionListener(e -> {
//                // String ingrediente = (String) cmbIngrediente.getSelectedItem();
//                // Adiciona visualmente na tabela temporária de ingredientes ou chama o controller
//                mostrarMensagem("btnIncluirAdiciona");
//            });
//        }
//
//        if (btnLimparIngredientes != null) {
//            btnLimparIngredientes.addActionListener(e -> {
//                // Limpa a tabela temporária de ingredientes extras da pizza atual
//                mostrarMensagem("btnLimparIngredientes");
//            });
//        }
//
//        if (btnRemoverItem != null) {
//            btnRemoverItem.addActionListener(e -> {
//                // int linhaSelecionada = tblItensPedido.getSelectedRow();
//                // if (linhaSelecionada >= 0) { ... controller.removerItem(id); }
//                mostrarMensagem("btnRemoverItem");
//                
//            });
//        }
//
//        if (btnFinalizarPedido != null) {
//            btnFinalizarPedido.addActionListener(e -> {
//                // controller.finalizarMontagemPedido(this.cpf);
//                JOptionPane.showMessageDialog(this, "Pedido finalizado com sucesso!");
//                
//                // Fecha a tela atual e retorna o foco para a PedidosView
//                this.dispose(); 
//            });
//        }
    }
    
    
    
    private void atualizarTotalPedido() { //importante ao adicionar cada item atualiza o total do pedido
        double total = 0.0;
        int indiceColunaPreco = 2; 

        for (int i = 0; i < tblItens.getRowCount(); i++) {
            try {
                Object valor = tblItens.getValueAt(i, indiceColunaPreco);
                if (valor != null) {
                    //converte string para double
                    double preco = Double.parseDouble(valor.toString().replace("R$", "").replace(".", "").replace(",", ".").trim());
                    total += preco;
                }
            } catch (NumberFormatException ex) {
                logger.warning("Erro ao converter valor da tabela para cálculo de total na linha " + i);
            }
        }

        lblTotal.setText("Total: "+ total  );
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        lblPedidoID = new javax.swing.JLabel();
        scrItens = new javax.swing.JScrollPane();
        tblItens = new javax.swing.JTable();
        btnRemoverItem = new javax.swing.JButton();
        lblClienteFulano = new javax.swing.JLabel();
        lblTamanhoPizza = new javax.swing.JLabel();
        lblIngrediente = new javax.swing.JLabel();
        cmbIngrediente = new javax.swing.JComboBox<>();
        scrIngredientesPizza = new javax.swing.JScrollPane();
        tblAdicionaisPizza = new javax.swing.JTable();
        btnAddPizzaAoPedido = new javax.swing.JButton();
        btnLimparIngredientes = new javax.swing.JButton();
        lblBebida = new javax.swing.JLabel();
        cmbBebida = new javax.swing.JComboBox<>();
        lblTamanhoBebida = new javax.swing.JLabel();
        cmbTamanhoBebida = new javax.swing.JComboBox<>();
        btnAddBebidaPedido = new javax.swing.JButton();
        btnFinalizarPedido = new javax.swing.JButton();
        btnIncluirAdiciona = new javax.swing.JButton();
        cmbListaPizzasBase = new javax.swing.JComboBox<>();
        lblTotal = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        lblPedidoID.setText("Pedido ID: #");

        tblItens.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID Item", "Descrição", "Quantidade", "Valor"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Object.class, java.lang.Object.class
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
        scrItens.setViewportView(tblItens);

        btnRemoverItem.setText("Remover Item");
        btnRemoverItem.addActionListener(this::btnRemoverItemActionPerformed);

        lblClienteFulano.setText("Cliente: Fulano");

        lblTamanhoPizza.setText("Pizza Base:");

        lblIngrediente.setText("Adicional:");

        cmbIngrediente.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "bacon", "mussarela", "azeitona", "Item 4" }));
        cmbIngrediente.addActionListener(this::cmbIngredienteActionPerformed);

        tblAdicionaisPizza.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Adicionais", "Quantidade"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        scrIngredientesPizza.setViewportView(tblAdicionaisPizza);

        btnAddPizzaAoPedido.setText("Adicionar Pizza ao Pedido");

        btnLimparIngredientes.setText("Limpar");

        lblBebida.setText("Bebida");

        cmbBebida.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Coca-Cola 2L", "Item 2", "Item 3", "Item 4" }));

        lblTamanhoBebida.setText("Tamanho");

        cmbTamanhoBebida.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnAddBebidaPedido.setText("Adicionar Bebida ao Pedido");

        btnFinalizarPedido.setText("Finalizar Pedido");

        btnIncluirAdiciona.setText("Incluir Adicional");

        cmbListaPizzasBase.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Quatro Queijos", "Calabresa", "da casa", "Item 4" }));

        lblTotal.setText("Total:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblPedidoID)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblClienteFulano, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(scrItens, javax.swing.GroupLayout.PREFERRED_SIZE, 832, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnRemoverItem)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(btnFinalizarPedido))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblTamanhoPizza)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cmbListaPizzasBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnIncluirAdiciona)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(btnAddPizzaAoPedido)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnLimparIngredientes))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblIngrediente)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cmbIngrediente, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(scrIngredientesPizza, javax.swing.GroupLayout.PREFERRED_SIZE, 299, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTamanhoBebida)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(lblBebida)))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbBebida, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(btnAddBebidaPedido))
                            .addComponent(cmbTamanhoBebida, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(lblTotal)
                .addGap(36, 36, 36))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPedidoID)
                    .addComponent(lblClienteFulano))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTamanhoPizza)
                    .addComponent(cmbListaPizzasBase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(86, 86, 86)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblBebida)
                            .addComponent(cmbBebida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblTamanhoBebida)
                            .addComponent(cmbTamanhoBebida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnAddBebidaPedido))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblIngrediente)
                            .addComponent(cmbIngrediente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(10, 10, 10)
                        .addComponent(btnIncluirAdiciona)
                        .addGap(3, 3, 3)
                        .addComponent(scrIngredientesPizza, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnAddPizzaAoPedido)
                            .addComponent(btnLimparIngredientes))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrItens, javax.swing.GroupLayout.PREFERRED_SIZE, 329, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addComponent(lblTotal)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnRemoverItem)
                    .addComponent(btnFinalizarPedido))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnRemoverItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoverItemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnRemoverItemActionPerformed

    private void cmbIngredienteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbIngredienteActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbIngredienteActionPerformed

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
        java.awt.EventQueue.invokeLater(() -> new ItensPedidoView().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddBebidaPedido;
    private javax.swing.JButton btnAddPizzaAoPedido;
    private javax.swing.JButton btnFinalizarPedido;
    private javax.swing.JButton btnIncluirAdiciona;
    private javax.swing.JButton btnLimparIngredientes;
    private javax.swing.JButton btnRemoverItem;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JComboBox<String> cmbBebida;
    private javax.swing.JComboBox<String> cmbIngrediente;
    private javax.swing.JComboBox<String> cmbListaPizzasBase;
    private javax.swing.JComboBox<String> cmbTamanhoBebida;
    private javax.swing.JLabel lblBebida;
    private javax.swing.JLabel lblClienteFulano;
    private javax.swing.JLabel lblIngrediente;
    private javax.swing.JLabel lblPedidoID;
    private javax.swing.JLabel lblTamanhoBebida;
    private javax.swing.JLabel lblTamanhoPizza;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JScrollPane scrIngredientesPizza;
    private javax.swing.JScrollPane scrItens;
    private javax.swing.JTable tblAdicionaisPizza;
    private javax.swing.JTable tblItens;
    // End of variables declaration//GEN-END:variables
}
