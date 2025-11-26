package br.com.moicano.lojinha.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private static final ConnectionFactory connectionFactory = H2ConnectionFactory.getInstance();

    public static Connection getConnection() throws SQLException {
        return connectionFactory.getConnection();
    }

    public static void closeConnection(Connection connection) throws SQLException {
        connectionFactory.closeConnection(connection);
    }

    public static void initDatabase() {
        String createCategoriasSQL = """
                CREATE TABLE IF NOT EXISTS categorias (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    descricao VARCHAR(255)
                )
                """;

        String createProdutosSQL = """
                CREATE TABLE produtos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    descricao VARCHAR(255),
                    preco DECIMAL(10, 2) NOT NULL,
                    quantidade INT NOT NULL,
                    categoria_id INT,
                    dosagem VARCHAR(100),
                    requer_receita BOOLEAN DEFAULT FALSE,
                    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
                )
                """;

        String createPedidosSQL = """
                CREATE TABLE pedidos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    data TIMESTAMP,
                    valor_total DECIMAL(10, 2),
                    cliente_nome VARCHAR(255),
                    status VARCHAR(100)
                )
                """;

        String createItensPedidoSQL = """
                CREATE TABLE itens_pedido (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    pedido_id INT,
                    produto_id INT,
                    quantidade INT,
                    preco_unitario DECIMAL(10, 2),
                    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
                    FOREIGN KEY (produto_id) REFERENCES produtos(id)
                )
                """;

        // --- NOVO: TABELA DE FAVORITOS ---
        String createFavoritosSQL = """
                CREATE TABLE favoritos (
                    produto_id INT PRIMARY KEY,
                    FOREIGN KEY (produto_id) REFERENCES produtos(id)
                )
                """;

        Connection conn = null;
        try {
            conn = getConnection();
            Statement stmt = conn.createStatement();

            // Apaga na ordem inversa (para não dar erro de chave)
            stmt.execute("DROP TABLE IF EXISTS favoritos"); // Apaga favoritos primeiro
            stmt.execute("DROP TABLE IF EXISTS itens_pedido");
            stmt.execute("DROP TABLE IF EXISTS pedidos");
            stmt.execute("DROP TABLE IF EXISTS produtos");
            stmt.execute("DROP TABLE IF EXISTS categorias");

            // Cria na ordem correta
            stmt.execute(createCategoriasSQL);
            stmt.execute(createProdutosSQL);
            stmt.execute(createPedidosSQL);
            stmt.execute(createItensPedidoSQL);
            stmt.execute(createFavoritosSQL); // Cria a tabela nova

            System.out.println("SUCESSO: Banco de dados inicializado com sucesso!");
        } catch (SQLException e) {
            System.err.println("ERRO: Erro ao inicializar banco de dados: " + e.getMessage());
        } finally {
            try {
                closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("ERRO: Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}