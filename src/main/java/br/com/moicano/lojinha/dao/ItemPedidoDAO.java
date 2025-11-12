package br.com.moicano.lojinha.dao;

import br.com.moicano.lojinha.database.DatabaseConnection;
import br.com.moicano.lojinha.model.ItemPedido;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Classe DAO (Data Access Object) para a entidade ItemPedido.
// Esta classe gerencia a persistência dos dados da tabela 'itens_pedido',
// que representa os produtos individuais dentro de um pedido maior.
public class ItemPedidoDAO {

    /**
     * Método CREATE (Criar).
     * Salva um item específico (um produto com sua quantidade e preço)
     * vinculado a um pedido existente no banco de dados.
     */
    public void criar(ItemPedido item) {
        // Define a instrução SQL de inserção com parâmetros (?) para
        // os IDs do pedido e produto, a quantidade e o preço no momento da compra.
        String sql = "INSERT INTO itens_pedido (pedido_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        // Usa try-with-resources para garantir o fechamento automático da conexão e do statement.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define os valores dos parâmetros (?) com base no objeto ItemPedido recebido.
            stmt.setInt(1, item.getPedidoId());
            stmt.setInt(2, item.getProdutoId());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPrecoUnitario());

            // Executa a inserção no banco de dados.
            stmt.executeUpdate();

        } catch (SQLException e) {
            // Em caso de erro, lança uma exceção para informar a camada de serviço.
            throw new RuntimeException("Erro ao salvar item do pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Método READ (Ler por ID do Pedido).
     * Busca todos os itens (produtos, quantidades, etc.) que pertencem
     * a um pedido específico, identificado pelo pedidoId.
     * Também busca o nome do produto para facilitar a exibição.
     */
    public List<ItemPedido> buscarPorPedidoId(Integer pedidoId) {
        // Inicializa a lista que conterá os itens do pedido.
        List<ItemPedido> itens = new ArrayList<>();

        // Instrução SQL usando JOIN.
        // (ip = alias para itens_pedido, p = alias para produtos)
        // 1. SELECT ip.*, p.nome as produto_nome: Seleciona todas as colunas de itens_pedido
        //    e a coluna 'nome' da tabela 'produtos' (renomeada para 'produto_nome').
        // 2. FROM itens_pedido ip JOIN produtos p ON ip.produto_id = p.id:
        //    Junta as duas tabelas onde o 'produto_id' do item é igual ao 'id' do produto.
        // 3. WHERE ip.pedido_id = ?: Filtra os resultados para trazer apenas os
        //    itens que pertencem ao ID do pedido fornecido.
        String sql = """
                SELECT ip.*, p.nome as produto_nome 
                FROM itens_pedido ip 
                JOIN produtos p ON ip.produto_id = p.id 
                WHERE ip.pedido_id = ?
                """;

        // Usa try-with-resources para a conexão e o PreparedStatement.
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Define o parâmetro (?) da cláusula WHERE (o ID do pedido).
            stmt.setInt(1, pedidoId);

            // Executa a consulta e armazena os resultados no ResultSet.
            try (ResultSet rs = stmt.executeQuery()) {

                // Itera sobre cada linha (item) retornada pela consulta.
                while (rs.next()) {
                    // Cria um novo objeto ItemPedido.
                    ItemPedido item = new ItemPedido();

                    // Popula o objeto com os dados da tabela 'itens_pedido'.
                    item.setId(rs.getInt("id"));
                    item.setPedidoId(rs.getInt("pedido_id"));
                    item.setProdutoId(rs.getInt("produto_id"));
                    item.setQuantidade(rs.getInt("quantidade"));
                    item.setPrecoUnitario(rs.getDouble("preco_unitario"));

                    // Popula o campo extra (produtoNome) obtido através do JOIN.
                    // Este campo não existe na tabela 'itens_pedido', só no objeto Java.
                    item.setProdutoNome(rs.getString("produto_nome"));

                    // Adiciona o item populado à lista.
                    itens.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar itens do pedido: " + e.getMessage(), e);
        }

        // Retorna a lista de itens encontrados (pode estar vazia se o pedido não tiver itens).
        return itens;
    }
}