package br.com.moicano.lojinha.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária central para gerenciar a conexão com o banco de dados
 * e para inicializar a estrutura (esquema) do banco.
 */
public class DatabaseConnection {

    // A 'connectionFactory' é a implementação real (neste caso, H2)
    // que sabe como criar e gerenciar as conexões.
    // É 'static final' para garantir que haja apenas uma fábrica (padrão Singleton).
    private static final ConnectionFactory connectionFactory = H2ConnectionFactory.getInstance();

    /**
     * Obtém uma nova conexão do banco de dados a partir da fábrica.
     * Este é o método que o resto da aplicação (os DAOs) usa.
     */
    public static Connection getConnection() throws SQLException {
        return connectionFactory.getConnection();
    }

    /**
     * Devolve/Fecha uma conexão.
     * Em implementações com Pool de Conexões, isso não fecharia a conexão
     * de fato, mas a devolveria ao pool.
     */
    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null) { // Verifica se a conexão não é nula antes de fechar
            connectionFactory.closeConnection(connection);
        }
    }

    /**
     * Método principal de inicialização do banco de dados.
     * Ele apaga todas as tabelas existentes (se houver) e as recria do zero.
     * Isso é útil para testes ou para a primeira execução da aplicação.
     */
    public static void initDatabase() {

        // --- DEFINIÇÃO DAS TABELAS (DDL - Data Definition Language) ---

        // Tabela 'pai'. Não depende de nenhuma outra.
        String createCategoriasSQL = """
                CREATE TABLE IF NOT EXISTS categorias (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    descricao VARCHAR(255)
                )
                """;

        // Tabela 'filha' de 'categorias'.
        String createProdutosSQL = """
                CREATE TABLE IF NOT EXISTS produtos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nome VARCHAR(100) NOT NULL,
                    descricao VARCHAR(255),
                    preco DECIMAL(10, 2) NOT NULL,
                    quantidade INT NOT NULL,
                    categoria_id INT,
                    dosagem VARCHAR(100),
                    requer_receita BOOLEAN DEFAULT FALSE,
                    
                    -- Define a restrição de Chave Estrangeira (FOREIGN KEY)
                    -- O campo 'categoria_id' desta tabela REFERENCIA o campo 'id' da tabela 'categorias'.
                    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
                )
                """;

        // Tabela 'pai' de 'itens_pedido'.
        String createPedidosSQL = """
                CREATE TABLE IF NOT EXISTS pedidos (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    data TIMESTAMP,
                    valor_total DECIMAL(10, 2),
                    cliente_nome VARCHAR(255),
                    status VARCHAR(100)
                )
                """;

        // Tabela 'filha' de 'pedidos' E 'produtos'.
        // Ela "conecta" as duas tabelas.
        String createItensPedidoSQL = """
                CREATE TABLE IF NOT EXISTS itens_pedido (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    pedido_id INT,
                    produto_id INT,
                    quantidade INT,
                    preco_unitario DECIMAL(10, 2),
                    
                    -- Chave estrangeira para 'pedidos'
                    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
                    -- Chave estrangeira para 'produtos'
                    FOREIGN KEY (produto_id) REFERENCES produtos(id)
                )
                """;

        //

        Connection conn = null;
        try {
            // Obtém a conexão para executar os comandos DDL
            conn = getConnection();
            Statement stmt = conn.createStatement();

            // --- ETAPA 1: LIMPEZA (DROP TABLES) ---
            // É crucial apagar as tabelas na ORDEM INVERSA da criação.
            // Tabelas 'filhas' (com FOREIGN KEYs) devem ser apagadas ANTES
            // das tabelas 'pai' (que elas referenciam).
            stmt.execute("DROP TABLE IF EXISTS itens_pedido"); // Filha de Pedidos e Produtos
            stmt.execute("DROP TABLE IF EXISTS pedidos");      // Pai de ItensPedido
            stmt.execute("DROP TABLE IF EXISTS produtos");    // Filha de Categorias, Pai de ItensPedido
            stmt.execute("DROP TABLE IF EXISTS categorias");  // Tabela Pai

            // --- ETAPA 2: CRIAÇÃO (CREATE TABLES) ---
            // A ordem de criação é o oposto da limpeza.
            // Tabelas 'pai' (sem dependências) são criadas PRIMEIRO.
            stmt.execute(createCategoriasSQL); // 1º (Pai)
            stmt.execute(createProdutosSQL);   // 2º (Depende de Categorias)
            stmt.execute(createPedidosSQL);    // 3º (Pai)
            stmt.execute(createItensPedidoSQL); // 4º (Depende de Pedidos e Produtos)

            System.out.println("SUCESSO: Banco de dados inicializado com sucesso!");
            // Mostra a URL de conexão (útil para depuração com H2)
            if (connectionFactory instanceof H2ConnectionFactory) {
                System.out.println("  Usando: " +
                        ((H2ConnectionFactory) connectionFactory).getUrl());
            }

        } catch (SQLException e) {
            // Captura qualquer erro de SQL (sintaxe errada, etc.)
            System.err.println("ERRO: Erro ao inicializar banco de dados: " + e.getMessage());
        } finally {
            // --- ETAPA 3: FECHAMENTO ---
            // O bloco 'finally' garante que a conexão será fechada
            // mesmo que ocorra um erro (SQLException) no bloco 'try'.
            try {
                closeConnection(conn);
            } catch (SQLException e) {
                System.err.println("ERRO: Erro ao fechar conexão: " + e.getMessage());
            }
        }
    }
}