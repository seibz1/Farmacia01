package br.com.moicano.lojinha.dao;

import br.com.moicano.lojinha.database.DatabaseConnection;
import br.com.moicano.lojinha.model.Categoria;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Classe DAO (Data Access Object) para a entidade Categoria.
// É responsável por centralizar todas as operações de banco de dados (CRUD)
// relacionadas à tabela 'categorias'.
public class CategoriaDAO {

    // Método CREATE (Criar)
    // Insere uma nova categoria no banco de dados.
    public void criar(Categoria categoria) {
        // Define a instrução SQL de inserção com parâmetros (?).
        String sql = "INSERT INTO categorias (nome, descricao) VALUES (?, ?)";

        // Utiliza try-with-resources para garantir que a conexão (conn) e
        // o PreparedStatement (stmt) sejam fechados automaticamente após o uso.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define os valores dos parâmetros (?) da query com base no objeto Categoria recebido.
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());

            // Executa a instrução SQL (INSERT).
            stmt.executeUpdate();

        } catch (SQLException e) {
            // Em caso de erro de banco de dados, lança uma RuntimeException
            // encapsulando a exceção original.
            throw new RuntimeException("Erro ao cadastrar categoria: " + e.getMessage(), e);
        }
    }

    // Método READ (Ler Todos)
    // Busca e retorna uma lista de todas as categorias cadastradas no banco.
    public List<Categoria> buscarTodas() {
        // Inicializa a lista que armazenará as categorias.
        List<Categoria> categorias = new ArrayList<>();
        // Define a instrução SQL de seleção.
        String sql = "SELECT * FROM categorias ORDER BY nome";

        // Usa try-with-resources para gerenciar os recursos JDBC.
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             // ResultSet (rs) armazena o resultado da consulta.
             ResultSet rs = stmt.executeQuery(sql)) {

            // Itera sobre cada linha (registro) retornada no ResultSet.
            while (rs.next()) {
                // Cria um objeto Categoria para cada linha.
                Categoria categoria = new Categoria();
                // Popula o objeto com os dados da linha atual do banco.
                categoria.setId(rs.getInt("id"));
                categoria.setNome(rs.getString("nome"));
                categoria.setDescricao(rs.getString("descricao"));
                // Adiciona o objeto à lista.
                categorias.add(categoria);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar categorias: " + e.getMessage(), e);
        }

        // Retorna a lista completa de categorias.
        return categorias;
    }

    // Método READ (Ler por ID)
    // Busca e retorna uma categoria específica com base no seu ID.
    public Categoria buscarPorId(int id) {
        // Define a instrução SQL de seleção com filtro (WHERE).
        String sql = "SELECT * FROM categorias WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define o parâmetro (?) da cláusula WHERE.
            stmt.setInt(1, id);

            // Executa a consulta e armazena no ResultSet.
            try (ResultSet rs = stmt.executeQuery()) {

                // Verifica se um registro foi encontrado.
                if (rs.next()) {
                    // Se sim, popula o objeto Categoria.
                    Categoria categoria = new Categoria();
                    categoria.setId(rs.getInt("id"));
                    categoria.setNome(rs.getString("nome"));
                    categoria.setDescricao(rs.getString("descricao"));
                    // Retorna a categoria encontrada.
                    return categoria;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar categoria: " + e.getMessage(), e);
        }

        // Se o 'if (rs.next())' for falso (nenhum registro encontrado), retorna null.
        return null;
    }

    // Método UPDATE (Atualizar)
    // Modifica os dados de uma categoria existente no banco.
    public void atualizar(Categoria categoria) {
        // Define a instrução SQL de atualização (UPDATE).
        String sql = "UPDATE categorias SET nome = ?, descricao = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define os parâmetros para a atualização (nome, descrição e o ID para o WHERE).
            stmt.setString(1, categoria.getNome());
            stmt.setString(2, categoria.getDescricao());
            stmt.setInt(3, categoria.getId());

            // Executa a atualização e armazena o número de linhas afetadas.
            int rowsAffected = stmt.executeUpdate();

            // Se 0 linhas foram afetadas, significa que o ID informado não existe.
            if (rowsAffected == 0) {
                throw new RuntimeException("Categoria não encontrada!");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar categoria: " + e.getMessage(), e);
        }
    }

    // Método DELETE (Deletar)
    // Remove uma categoria do banco de dados com base no ID.
    public void deletar(int id) {
        // SQL para verificar a integridade referencial:
        // Verifica se existem produtos (tabela 'produtos') associados a esta categoria.
        String checkSql = "SELECT COUNT(*) FROM produtos WHERE categoria_id = ?";
        // SQL para deletar a categoria.
        String deleteSql = "DELETE FROM categorias WHERE id = ?";

        // Obtém uma única conexão para realizar as duas operações.
        try (Connection conn = DatabaseConnection.getConnection()) {

            // --- Etapa 1: Verificação de Integridade ---
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, id);
                ResultSet rs = checkStmt.executeQuery();

                // Se o resultado (COUNT) for maior que 0, lança uma exceção
                // para impedir a exclusão (regra de negócio).
                if (rs.next() && rs.getInt(1) > 0) {
                    throw new RuntimeException("Não é possível excluir! Existem produtos vinculados a esta categoria.");
                }
            }

            // --- Etapa 2: Deleção (só ocorre se a Etapa 1 passar) ---
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, id);

                // Executa a deleção.
                int rowsAffected = deleteStmt.executeUpdate();

                // Se 0 linhas foram afetadas, o ID não foi encontrado.
                if (rowsAffected == 0) {
                    throw new RuntimeException("Categoria não encontrada!");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover categoria: " + e.getMessage(), e);
        }
    }
}