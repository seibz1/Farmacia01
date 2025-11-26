package br.com.moicano.lojinha.dao;

import br.com.moicano.lojinha.database.DatabaseConnection;
import br.com.moicano.lojinha.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FavoritoDAO {

    public void adicionar(int idProduto) {
        String sql = "INSERT INTO favoritos (produto_id) VALUES (?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProduto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            // Se tentar adicionar o mesmo favorito duas vezes, ignoramos o erro (opcional)
            if (!e.getMessage().contains("Primary key")) {
                throw new RuntimeException("Erro ao favoritar produto: " + e.getMessage(), e);
            }
        }
    }

    public void remover(int idProduto) {
        String sql = "DELETE FROM favoritos WHERE produto_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idProduto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover favorito: " + e.getMessage(), e);
        }
    }

    public List<Produto> listarFavoritos() {
        List<Produto> favoritos = new ArrayList<>();
        // Faz um JOIN para pegar os dados completos do produto que está nos favoritos
        String sql = """
                SELECT p.*, c.nome as categoria_nome 
                FROM favoritos f
                JOIN produtos p ON f.produto_id = p.id
                LEFT JOIN categorias c ON p.categoria_id = c.id
                ORDER BY p.nome
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setCategoriaId(rs.getInt("categoria_id"));
                produto.setCategoriaNome(rs.getString("categoria_nome"));
                produto.setDosagem(rs.getString("dosagem"));
                produto.setRequerReceita(rs.getBoolean("requer_receita"));
                favoritos.add(produto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar favoritos: " + e.getMessage(), e);
        }
        return favoritos;
    }
}