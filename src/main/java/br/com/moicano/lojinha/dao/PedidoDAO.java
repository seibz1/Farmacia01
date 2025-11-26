package br.com.moicano.lojinha.dao;

import br.com.moicano.lojinha.database.DatabaseConnection;
import br.com.moicano.lojinha.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAO {

    public Integer criar(Pedido pedido) {
        String sql = "INSERT INTO pedidos (data, valor_total, cliente_nome, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(pedido.getData()));
            stmt.setDouble(2, pedido.getValorTotal());
            stmt.setString(3, pedido.getClienteNome());
            stmt.setString(4, pedido.getStatus());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar pedido: " + e.getMessage(), e);
        }
    }

    public List<Pedido> buscarTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        String sql = "SELECT * FROM pedidos ORDER BY data DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                pedido.setData(rs.getTimestamp("data").toLocalDateTime());
                pedido.setValorTotal(rs.getDouble("valor_total"));
                pedido.setClienteNome(rs.getString("cliente_nome"));
                pedido.setStatus(rs.getString("status"));
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return pedidos;
    }

    // --- CORREÇÃO AQUI ---
    public List<Pedido> buscarPedidosAtivos() {
        List<Pedido> pedidos = new ArrayList<>();
        // MUDANÇA: Trocámos "IS NOT" por "<>" (diferente)
        String sql = "SELECT * FROM pedidos WHERE status <> 'ENTREGUE' ORDER BY data ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                pedido.setData(rs.getTimestamp("data").toLocalDateTime());
                pedido.setValorTotal(rs.getDouble("valor_total"));
                pedido.setClienteNome(rs.getString("cliente_nome"));
                pedido.setStatus(rs.getString("status"));
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos ativos: " + e.getMessage(), e);
        }
        return pedidos;
    }

    public void atualizarStatus(Integer pedidoId, String novoStatus) {
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);
            stmt.setInt(2, pedidoId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do pedido: " + e.getMessage(), e);
        }
    }
}