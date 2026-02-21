/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.trabalho.delivery.infraestrutura.repositories;

/**
 *
 * @author Yuri
 */
import com.mycompany.trabalho.delivery.dominio.adapter.ConsoleLogAdapter;
import com.mycompany.trabalho.delivery.dominio.model.bebida.Bebida;
import com.mycompany.trabalho.delivery.dominio.model.cliente.Cliente;
import com.mycompany.trabalho.delivery.dominio.model.pedido.IPedidoState;
import com.mycompany.trabalho.delivery.dominio.model.pedido.Item;
import com.mycompany.trabalho.delivery.dominio.model.pedido.Pedido;
import com.mycompany.trabalho.delivery.dominio.model.pedido.PedidoCanceladoState;
import com.mycompany.trabalho.delivery.dominio.model.pedido.PedidoEntregueState;
import com.mycompany.trabalho.delivery.dominio.model.pedido.PedidoPendenteState;
import com.mycompany.trabalho.delivery.dominio.model.pedido.PedidoPreparadoState;
import com.mycompany.trabalho.delivery.dominio.model.pedido.PedidoProntoState;
import com.mycompany.trabalho.delivery.dominio.model.pizza.CalabresaBuilder;
import com.mycompany.trabalho.delivery.dominio.model.pizza.Ingrediente;
import com.mycompany.trabalho.delivery.dominio.model.pizza.IngredienteDecorator;
import com.mycompany.trabalho.delivery.dominio.model.pizza.MassaBase;
import com.mycompany.trabalho.delivery.dominio.model.pizza.PizzaComponente;
import com.mycompany.trabalho.delivery.dominio.model.pizza.PizzaioloDiretor;
import com.mycompany.trabalho.delivery.dominio.model.shared.Endereco;
import com.mycompany.trabalho.delivery.dominio.port.IPedidoRepository;
import com.mycompany.trabalho.delivery.infraestrutura.ConexaoSingleton;
import com.mycompany.trabalho.delivery.infraestrutura.DbInitializer;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepositorySQLite implements IPedidoRepository {

    @Override
    public void salvarPedido(Pedido pedido) {
        if (pedido.getId() > 0) {
            String sqlUpdate = "UPDATE pedidos SET status = ? WHERE id = ?";
            try (Connection conexao = ConexaoSingleton.getConexao();
                 PreparedStatement stmt = conexao.prepareStatement(sqlUpdate)) {
                 
                stmt.setString(1, pedido.getEstado().getDescricao());
                stmt.setLong(2, pedido.getId());
                stmt.executeUpdate();
                System.out.println("Status do pedido " + pedido.getId() + " atualizado no banco.");
                
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return; // O "return" faz o código parar aqui e não executar o INSERT abaixo!
        }
        
        String sqlPedido = "INSERT INTO pedidos(cliente_id, valor_total, status) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO itens_pedido(pedido_id, tipo, tamanho, valor_base, descricao_visual) VALUES (?, ?, ?, ?, ?)";
        String sqlAdicional = "INSERT INTO itens_adicionais(item_pedido_id, nome, valor_adicional) VALUES (?, ?, ?)";

        Connection conexao = null;
        try{
            conexao = ConexaoSingleton.getConexao();
            conexao.setAutoCommit(false);

            int clienteId = new ClienteRepositorySQLite().buscarIdPorCpf(pedido.getCliente().getCpf());
            if (clienteId == -1) {
                throw new SQLException("Cliente não encontrado na base de dados para o CPF: " + pedido.getCliente().getCpf());
            }

            int pedidoId;
            try (PreparedStatement statement = conexao.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                statement.setInt(1, clienteId);
                statement.setDouble(2, pedido.getValorTotal());
                statement.setString(3, pedido.getEstado().getDescricao());
                
                statement.executeUpdate();
                
                try (ResultSet keys = statement.getGeneratedKeys()) {
                    if (keys.next()) {
                        pedidoId = keys.getInt(1);
                        definirIdPedido(pedido, pedidoId); 
                    } else {
                        throw new SQLException("Falha ao criar pedido, nenhum ID obtido.");
                    }
                }
            }

            for (Item item : pedido.getItens()) {
                int itemPedidoId;
                
                if (item instanceof Bebida) {
                    try (PreparedStatement statementItem = conexao.prepareStatement(sqlItem, Statement.RETURN_GENERATED_KEYS)) {
                        statementItem.setInt(1, pedidoId);
                        statementItem.setString(2, "BEBIDA");
                        statementItem.setString(3, "Padrao");
                        statementItem.setDouble(4, item.getPreco());
                        statementItem.setString(5, item.toString());
                        statementItem.executeUpdate();
                    }
                }else if(item instanceof PizzaComponente) {
                    PizzaComponente pizza = (PizzaComponente) item;
                    
                    List<TempAdicional> adicionais = new ArrayList<>();
                    PizzaComponente atual = pizza;
                    
                    while (atual instanceof IngredienteDecorator) {
                        PizzaComponente anterior = atual.desfazer();
                        
                        double precoIngrediente = atual.getPreco() - anterior.getPreco();
                        
                        String descAtual = atual.getDescricao();
                        String descAnterior = anterior.getDescricao();
                        String nomeIngrediente = descAtual.replace(descAnterior + ", ", "");
                        
                        adicionais.add(new TempAdicional(nomeIngrediente, precoIngrediente));
                        atual = anterior;
                    }
                    
                    try (PreparedStatement statementItem = conexao.prepareStatement(sqlItem, Statement.RETURN_GENERATED_KEYS)) {
                        statementItem.setInt(1, pedidoId);
                        statementItem.setString(2, "PIZZA");
                        statementItem.setString(3, "Padrao"); 
                        statementItem.setDouble(4, atual.getPreco());
                        statementItem.setString(5, atual.getDescricao());
                        statementItem.executeUpdate();
                        
                        try (ResultSet keys = statementItem.getGeneratedKeys()) {
                            if (keys.next()) {
                                itemPedidoId = keys.getInt(1);
                                
                                try (PreparedStatement statementAdic = conexao.prepareStatement(sqlAdicional)) {
                                    for (TempAdicional adic : adicionais) {
                                        statementAdic.setInt(1, itemPedidoId);
                                        statementAdic.setString(2, adic.nome);
                                        statementAdic.setDouble(3, adic.valor);
                                        statementAdic.executeUpdate();
                                    }
                                }
                            }
                        }
                    }
                }
            }

            conexao.commit();
            System.out.println("Pedido salvo com sucesso. ID: " + pedidoId);

        } catch (SQLException | IllegalAccessException | NoSuchFieldException e) {
            try {
                if (conexao != null) conexao.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar pedido: " + e.getMessage());
        } finally {
            try {
                if (conexao != null) conexao.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Pedido buscarPedidoPorId(int id) {
        String sqlPedido = "SELECT * FROM pedidos WHERE id = ?";
        String sqlItens = "SELECT * FROM itens_pedido WHERE pedido_id = ?";
        String sqlAdicionais = "SELECT * FROM itens_adicionais WHERE item_pedido_id = ?";

        try (Connection conexao = ConexaoSingleton.getConexao();
             PreparedStatement statement = conexao.prepareStatement(sqlPedido)) {

            statement.setInt(1, id);
            ResultSet rs = statement.executeQuery();

            if (rs.next()) {
                int clienteId = rs.getInt("cliente_id");
                String statusDesc = rs.getString("status");

                Cliente cliente = buscarClientePorIdInterno(conexao, clienteId);
                
                IPedidoState estado = mapearEstado(statusDesc);

                List<Item> itens = new ArrayList<>();
                try (PreparedStatement statementItens = conexao.prepareStatement(sqlItens)) {
                    statementItens.setInt(1, id);
                    ResultSet rsItens = statementItens.executeQuery();

                    while (rsItens.next()) {
                        int itemPedidoId = rsItens.getInt("id");
                        String tipo = rsItens.getString("tipo");
                        double valorBase = rsItens.getDouble("valor_base");
                        String descVisual = rsItens.getString("descricao_visual");

                        if ("BEBIDA".equalsIgnoreCase(tipo)) {
                            itens.add(new Bebida(descVisual, valorBase));
                        } else if ("PIZZA".equalsIgnoreCase(tipo)) {
                            PizzaComponente pizza = new MassaBase(descVisual, valorBase);
                            
                            try (PreparedStatement stmtAdic = conexao.prepareStatement(sqlAdicionais)) {
                                stmtAdic.setInt(1, itemPedidoId);
                                ResultSet rsAdic = stmtAdic.executeQuery();
                                while (rsAdic.next()) {
                                    String nomeIngrediente = rsAdic.getString("nome");
                                    double valorIngrediente = rsAdic.getDouble("valor_adicional");
                                    pizza = new Ingrediente(pizza, nomeIngrediente, valorIngrediente);
                                }
                            }
                            itens.add(pizza);
                        }
                    }
                }

                Pedido pedido = new Pedido(new ConsoleLogAdapter(), cliente, itens, estado);
                definirIdPedido(pedido, id);
                return pedido;
            }

        } catch (SQLException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void deletarPedido(int id) {
        String sql = "DELETE FROM pedidos WHERE id = ?";
        try (Connection conexao = ConexaoSingleton.getConexao();
             PreparedStatement statement = conexao.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            statement.executeUpdate();
            System.out.println("Pedido " + id + " deletado.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private Cliente buscarClientePorIdInterno(Connection conexao, int id) throws SQLException {
        String sql = "SELECT * FROM clientes WHERE id = ?";
        try (PreparedStatement statement = conexao.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet rset = statement.executeQuery();
            if (rset.next()) {
                String nome = rset.getString("nome");
                String cpf = rset.getString("cpf");
                String email = rset.getString("email");
                String cidade = rset.getString("cidade");
                String bairro = rset.getString("bairro");
                String rua = rset.getString("rua");
                String numero = rset.getString("numero");
                Endereco endereco = new Endereco(cidade, bairro, rua, numero);
                return new Cliente(nome, cpf, email, endereco);
            }
        }
        return null;
    }
    
    @Override
    public List<Pedido> buscarPedidosPorCpf(String cpf) {
        List<Pedido> pedidos = new ArrayList<>();
                String sql= "SELECT p.id FROM pedidos p " +
                     "JOIN clientes c ON p.cliente_id = c.id " +
                     "WHERE c.cpf = ?";

        try(Connection conexao = ConexaoSingleton.getConexao();
            PreparedStatement statement = conexao.prepareStatement(sql)) {

            statement.setString(1, cpf);
            ResultSet rset = statement.executeQuery();

            while(rset.next()) {
                int id = rset.getInt("id");
                
                Pedido pedido = buscarPedidoPorId(id);    
                if(pedido != null){
                    pedidos.add(pedido);
                }
            }

        } catch(SQLException e) {
            e.printStackTrace();
        }
        
        return pedidos;
    }

    private IPedidoState mapearEstado(String descricao) {
        if (descricao == null) return new PedidoPendenteState();
        
        
        String descUpper = descricao.toUpperCase();
        if (descUpper.contains("PENDEN")) return new PedidoPendenteState();
        if (descUpper.contains("PREPARA")) return new PedidoPreparadoState();
        if (descUpper.contains("PRONT")) return new PedidoProntoState();
        if (descUpper.contains("ENTREG")) return new PedidoEntregueState();
        if (descUpper.contains("CANCEL")) return new PedidoCanceladoState();
        
        return new PedidoPendenteState();
    }

    private void definirIdPedido(Pedido pedido, long id) throws NoSuchFieldException, IllegalAccessException {
        Field idField = Pedido.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(pedido, id);
    }
    
    private static class TempAdicional {
        String nome;
        double valor;
        TempAdicional(String nome, double valor) { this.nome = nome; this.valor = valor; }
    }
}
