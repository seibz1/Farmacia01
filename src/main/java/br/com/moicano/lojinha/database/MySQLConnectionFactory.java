/*






Esse código não está em uso, 
está apenas como exemplo para alteração do SGBD.

Bastaria alterar em DatabaseConnection:

private static final ConnectionFactory connectionFactory = H2ConnectionFactory.getInstance();

por:

private static final ConnectionFactory connectionFactory = MySQLConnectionFactory.getInstance();

Exemplo clássico de desacoplamento

*/

package br.com.moicano.lojinha.database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnectionFactory implements ConnectionFactory {
    private static final String URL = "jdbc:mysql://localhost:3306/lojinha";
    private static final String USER = "root";
    private static final String PASSWORD = "senha123";

    private static MySQLConnectionFactory instance;

    private MySQLConnectionFactory() {}

    public static MySQLConnectionFactory getInstance() {
        if (instance == null) {
            synchronized (MySQLConnectionFactory.class) {
                if (instance == null) {
                    instance = new MySQLConnectionFactory();
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}