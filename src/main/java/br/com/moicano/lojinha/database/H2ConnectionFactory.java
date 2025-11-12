package br.com.moicano.lojinha.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class H2ConnectionFactory implements ConnectionFactory {
    private static final String URL = "jdbc:h2:./lojinha";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static H2ConnectionFactory instance;

    // Singleton para garantir uma única instância da factory
    private H2ConnectionFactory() {}

    public static H2ConnectionFactory getInstance() {
        if (instance == null) {
            synchronized (H2ConnectionFactory.class) {
                if (instance == null) {
                    instance = new H2ConnectionFactory();
                }
            }
        }
        return instance;
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            // Opcional para H2 moderno, boa prática em manter
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver H2 não encontrado", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    @Override
    public void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // Útil para logs
    public String getUrl() {
        return URL;
    }

    public String getUser() {
        return USER;
    }
}