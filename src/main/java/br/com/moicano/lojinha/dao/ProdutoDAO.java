package br.com.moicano.lojinha.dao;

import br.com.moicano.lojinha.database.DatabaseConnection;
import br.com.moicano.lojinha.model.Produto;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Classe DAO (Data Access Object) para a entidade Produto.
// É responsável por todas as operações de banco de dados (CRUD)
// relacionadas à tabela 'produtos'.
public class ProdutoDAO {

    /**
     * Método CREATE (Criar).
     * Insere um novo produto no banco de dados, incluindo os novos campos
     * 'dosagem' e 'requer_receita'.
     */
    public void criar(Produto produto) {
        // Define a instrução SQL de inserção com todos os campos.
        String sql = "INSERT INTO produtos (nome, descricao, preco, quantidade, categoria_id, dosagem, requer_receita) VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Usa try-with-resources para garantir o fechamento da conexão e statement.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define os valores dos parâmetros (?) com base no objeto Produto.
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidade());

            // Tratamento especial para 'categoria_id', que pode ser nula (NULL).
            // Se o ID da categoria não for nulo, define o Int.
            if (produto.getCategoriaId() != null) {
                stmt.setInt(5, produto.getCategoriaId());
            } else {
                // Se for nulo, informa ao JDBC para inserir um NULL do tipo INTEGER.
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, produto.getDosagem());
            stmt.setBoolean(7, produto.isRequerReceita());

            // Executa a inserção.
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cadastrar produto: " + e.getMessage(), e);
        }
    }

    /**
     * Método READ (Ler Todos).
     * Busca todos os produtos e também o nome da categoria associada
     * usando um LEFT JOIN.
     */
    public List<Produto> buscarTodos() {
        List<Produto> produtos = new ArrayList<>();
        // SQL com LEFT JOIN:
        // 1. SELECT p.*, c.nome as categoria_nome: Seleciona todas as colunas
        //    de 'produtos' (alias 'p') e a coluna 'nome' de 'categorias' (alias 'c'),
        //    renomeando-a para 'categoria_nome'.
        // 2. LEFT JOIN ...: Junta as tabelas. O LEFT JOIN garante que,
        //    mesmo se um produto não tiver categoria (categoria_id = NULL),
        //    ele AINDA ASSIM será listado (com categoria_nome = NULL).
        String sql = """
                SELECT p.*, c.nome as categoria_nome 
                FROM produtos p 
                LEFT JOIN categorias c ON p.categoria_id = c.id 
                ORDER BY p.id
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Itera sobre o resultado da consulta.
            while (rs.next()) {
                // Cria e popula o objeto Produto para cada linha.
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setDescricao(rs.getString("descricao"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setQuantidade(rs.getInt("quantidade"));
                produto.setCategoriaId(rs.getInt("categoria_id"));
                // Define o nome da categoria (obtido via JOIN).
                produto.setCategoriaNome(rs.getString("categoria_nome"));
                produto.setDosagem(rs.getString("dosagem"));
                produto.setRequerReceita(rs.getBoolean("requer_receita"));
                produtos.add(produto);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return produtos;
    }

    /**
     * Método READ (Ler por ID).
     * Busca um produto específico pelo seu ID, também incluindo o nome
     * da categoria (via LEFT JOIN).
     */
    public Produto buscarPorId(int id) {
        // A lógica do SQL é a mesma do 'buscarTodos', mas com a
        // cláusula 'WHERE p.id = ?' para filtrar por um ID específico.
        String sql = """
                SELECT p.*, c.nome as categoria_nome 
                FROM produtos p 
                LEFT JOIN categorias c ON p.categoria_id = c.id 
                WHERE p.id = ?
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define o parâmetro (?) do WHERE.
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                // Se encontrou um resultado (rs.next() é verdadeiro)...
                if (rs.next()) {
                    // ...popula o objeto Produto.
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
                    return produto;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }
        // Se o 'if' falhar (ID não encontrado), retorna null.
        return null;
    }

    /**
     * Método READ (Ler por Categoria).
     * Busca todos os produtos que pertencem a uma categoria específica.
     */
    public List<Produto> buscarPorCategoria(int categoriaId) {
        List<Produto> produtos = new ArrayList<>();
        // SQL similar aos anteriores, mas o filtro (WHERE) é no 'p.categoria_id'.
        String sql = """
                SELECT p.*, c.nome as categoria_nome 
                FROM produtos p 
                LEFT JOIN categorias c ON p.categoria_id = c.id 
                WHERE p.categoria_id = ? 
                ORDER BY p.nome
                """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define o ID da categoria no filtro WHERE.
            stmt.setInt(1, categoriaId);

            try (ResultSet rs = stmt.executeQuery()) {
                // Itera sobre todos os produtos encontrados para aquela categoria.
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
                    produtos.add(produto);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produtos por categoria: " + e.getMessage(), e);
        }
        return produtos;
    }

    /**
     * Método UPDATE (Atualizar).
     * Atualiza todos os dados de um produto existente, com base no ID.
     */
    public void atualizar(Produto produto) {
        // SQL de atualização (UPDATE) para todos os campos, filtrando por ID.
        String sql = "UPDATE produtos SET nome = ?, descricao = ?, preco = ?, quantidade = ?, categoria_id = ?, dosagem = ?, requer_receita = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define os parâmetros (1 a 7) para os campos a serem atualizados.
            stmt.setString(1, produto.getNome());
            stmt.setString(2, produto.getDescricao());
            stmt.setDouble(3, produto.getPreco());
            stmt.setInt(4, produto.getQuantidade());

            // Novamente, trata o 'categoria_id' como nulo (nullable).
            if (produto.getCategoriaId() != null) {
                stmt.setInt(5, produto.getCategoriaId());
            } else {
                stmt.setNull(5, Types.INTEGER);
            }

            stmt.setString(6, produto.getDosagem());
            stmt.setBoolean(7, produto.isRequerReceita());

            // Define o último parâmetro (8), que é o ID do 'WHERE'.
            stmt.setInt(8, produto.getId());

            // Verifica se alguma linha foi realmente atualizada.
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                // Se 0 linhas foram afetadas, o ID do produto não foi encontrado.
                throw new RuntimeException("Produto não encontrado!");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    /**
     * Método DELETE (Deletar).
     * Remove um produto do banco de dados com base no seu ID.
     */
    public void deletar(int id) {
        // SQL de deleção simples.
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define o ID do produto a ser deletado.
            stmt.setInt(1, id);

            // Executa a deleção e verifica se funcionou.
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                // Se 0 linhas foram afetadas, o ID não foi encontrado.
                throw new RuntimeException("Produto não encontrado!");
            }
        } catch (SQLException e) {
            // Nota: Se este produto estiver em um 'item_pedido' e o banco tiver
            // uma restrição de Chave Estrangeira (FOREIGN KEY) configurada,
            // esta exceção (SQLException) será lançada, protegendo a
            // integridade dos dados (o que é bom).
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage(), e);
        }
    }
}