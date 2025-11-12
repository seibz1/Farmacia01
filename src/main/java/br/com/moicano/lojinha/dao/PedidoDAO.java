package br.com.moicano.lojinha.dao;

import br.com.moicano.lojinha.database.DatabaseConnection;
import br.com.moicano.lojinha.model.Pedido;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Classe DAO (Data Access Object) para a entidade Pedido.
// Gerencia as operações de CRUD (Criar, Ler, Atualizar, Deletar)
// para a tabela 'pedidos' no banco de dados.
public class PedidoDAO {

    /**
     * Método CREATE (Criar).
     * Insere um novo pedido no banco de dados.
     * Importante: Este método retorna o ID (chave primária) gerado pelo banco
     * para o pedido que acabou de ser criado.
     */
    public Integer criar(Pedido pedido) {
        // A instrução SQL agora inclui o campo 'status', que será salvo
        // (provavelmente com um valor inicial como "PENDENTE" ou "EM PREPARO").
        String sql = "INSERT INTO pedidos (data, valor_total, cliente_nome, status) VALUES (?, ?, ?, ?)";

        // 1. Usa try-with-resources para gerenciar a conexão e o PreparedStatement.
        // 2. Statement.RETURN_GENERATED_KEYS: Informa ao JDBC que queremos
        //    recuperar as chaves (como o ID auto-incremento) geradas por esta inserção.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            // Converte o LocalDateTime do Java para Timestamp do SQL.
            stmt.setTimestamp(1, Timestamp.valueOf(pedido.getData()));
            stmt.setDouble(2, pedido.getValorTotal());
            stmt.setString(3, pedido.getClienteNome());
            // Define o status do pedido (ex: "PENDENTE").
            stmt.setString(4, pedido.getStatus());

            // Executa a inserção.
            int rowsAffected = stmt.executeUpdate();

            // Se a inserção funcionou (1 linha afetada)...
            if (rowsAffected > 0) {
                // ...tenta obter as chaves geradas.
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        // Retorna a primeira chave gerada (que é o ID do pedido).
                        return rs.getInt(1);
                    }
                }
            }
            // Retorna null se a inserção falhar ou não retornar um ID.
            return null;

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Método READ (Ler Todos).
     * Busca e retorna uma lista de todos os pedidos cadastrados,
     * ordenados pela data mais recente (DESC).
     */
    public List<Pedido> buscarTodos() {
        List<Pedido> pedidos = new ArrayList<>();
        // Ordena por data descendente (os mais novos primeiro).
        String sql = "SELECT * FROM pedidos ORDER BY data DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Itera sobre cada linha (pedido) retornada pela consulta.
            while (rs.next()) {
                // Cria e popula o objeto Pedido.
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                // Converte o Timestamp do SQL de volta para LocalDateTime do Java.
                pedido.setData(rs.getTimestamp("data").toLocalDateTime());
                pedido.setValorTotal(rs.getDouble("valor_total"));
                pedido.setClienteNome(rs.getString("cliente_nome"));
                // Lê o status do banco e o armazena no objeto.
                pedido.setStatus(rs.getString("status"));

                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage(), e);
        }
        return pedidos;
    }

    // --- NOVO MÉTODO ---
    /**
     * Método READ (Ler Ativos).
     * Busca todos os pedidos que *não* estão com o status 'ENTREGUE'.
     * Útil para telas de acompanhamento (ex: painel do admin ou app do entregador).
     */
    public List<Pedido> buscarPedidosAtivos() {
        List<Pedido> pedidos = new ArrayList<>();

        // A cláusula "WHERE status IS NOT 'ENTREGUE'" filtra apenas os pedidos
        // que ainda precisam de alguma ação.
        // "ORDER BY data ASC" mostra os pedidos mais antigos primeiro (prioridade).
        String sql = "SELECT * FROM pedidos WHERE status IS NOT 'ENTREGUE' ORDER BY data ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                // O processo de popular o objeto é idêntico ao 'buscarTodos'.
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

    // --- NOVO MÉTODO ---
    /**
     * Método UPDATE (Atualizar Status).
     * Altera *apenas* o status de um pedido específico, identificado pelo seu ID.
     * (Ex: Mudar de "PENDENTE" para "EM ROTA" ou de "EM ROTA" para "ENTREGUE").
     */
    public void atualizarStatus(Integer pedidoId, String novoStatus) {
        // SQL focado em atualizar apenas a coluna 'status' de um 'id' específico.
        String sql = "UPDATE pedidos SET status = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define os parâmetros: o novo status (String) e o ID do pedido (Integer).
            stmt.setString(1, novoStatus);
            stmt.setInt(2, pedidoId);

            // Executa a atualização. Não precisamos verificar as linhas afetadas
            // neste caso, embora fosse uma boa prática.
            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar status do pedido: " + e.getMessage(), e);
        }
    }
}