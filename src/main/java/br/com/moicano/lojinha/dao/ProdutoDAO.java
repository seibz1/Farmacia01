package br.com.moicano.lojinha.dao;

import br.com.moicano.lojinha.database.DatabaseConnection;
import br.com.moicano.lojinha.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {

    public void criar(Produto produto) {
        String sql = "INSERT INTO produtos (nome, descricao, preco, quantidade, categoria_id, dosagem, requer_receita) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidade());

            // --- CORREÇÃO DE SEGURANÇA ---
            // Se o ID for Nulo ou Zero, salvamos como NULL no banco
            if (produto.getCategoriaId() != null && produto.getCategoriaId() != 0) {
                stmt.setInt(5, produto.getCategoriaId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, produto.getDosagem());
            stmt.setBoolean(7, produto.isRequerReceita());

            stmt.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar produto: " + e.getMessage(), e);
        }
    }

    public List<Produto> buscarTodos() {
        List<Produto> produtos = new ArrayList<>();
        String sql = """
                SELECT p.*, c.nome as categoria_nome 
                FROM produtos p 
                LEFT JOIN categorias c ON p.categoria_id = c.id 
                ORDER BY p.id
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto produto = mapResultSetToProduto(rs);
                produtos.add(produto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }

        return produtos;
    }

    public Produto buscarPorId(int id) {
        String sql = """
                SELECT p.*, c.nome as categoria_nome 
                FROM produtos p 
                LEFT JOIN categorias c ON p.categoria_id = c.id 
                WHERE p.id = ?
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToProduto(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }

        return null;
    }

    public List<Produto> buscarPorCategoria(int categoriaId) {
        List<Produto> produtos = new ArrayList<>();
        String sql = """
                SELECT p.*, c.nome as categoria_nome 
                FROM produtos p 
                LEFT JOIN categorias c ON p.categoria_id = c.id 
                WHERE p.categoria_id = ? 
                ORDER BY p.nome
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, categoriaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Produto produto = mapResultSetToProduto(rs);
                produtos.add(produto);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por categoria: " + e.getMessage(), e);
        }

        return produtos;
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, quantidade = ?, categoria_id = ?, dosagem = ?, requer_receita = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidade());

            // --- CORREÇÃO CRÍTICA (A que resolveu o seu erro) ---
            // Verifica se é 0 antes de tentar salvar
            if (produto.getCategoriaId() != null && produto.getCategoriaId() != 0) {
                stmt.setInt(5, produto.getCategoriaId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, produto.getDosagem());
            stmt.setBoolean(7, produto.isRequerReceita());
            stmt.setInt(8, produto.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Produto não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM produtos WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Produto não encontrado!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage(), e);
        }
    }

    // Método auxiliar para evitar repetição de código ao ler do banco
    private Produto mapResultSetToProduto(ResultSet rs) throws SQLException {
        Produto produto = new Produto();
        produto.setId(rs.getInt("id"));
        produto.setNome(rs.getString("nome"));
        produto.setDescricao(rs.getString("descricao"));
        produto.setPreco(rs.getDouble("preco"));
        produto.setQuantidade(rs.getInt("quantidade"));

        // O getInt retorna 0 se for null no banco, mas precisamos saber se era null mesmo
        int catId = rs.getInt("categoria_id");
        if (rs.wasNull()) {
            produto.setCategoriaId(null); // ou 0, dependendo da sua lógica, mas null é mais correto
        } else {
            produto.setCategoriaId(catId);
        }

        produto.setCategoriaNome(rs.getString("categoria_nome"));
        produto.setDosagem(rs.getString("dosagem"));
        produto.setRequerReceita(rs.getBoolean("requer_receita"));
        return produto;
    }
}