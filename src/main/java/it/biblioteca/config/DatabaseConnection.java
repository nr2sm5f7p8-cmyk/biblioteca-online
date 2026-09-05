package it.biblioteca.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/biblioteca_online";

    private static final String USER = "root";

    private static final String PASSWORD =
            System.getenv("BIBLIOTECA_DB_PASSWORD");

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException(
                    "Driver MySQL non trovato", e);
        }

        if (PASSWORD == null) {
            throw new SQLException(
                    "Variabile BIBLIOTECA_DB_PASSWORD non configurata");
        }

        return DriverManager.getConnection(
                URL,
                USER,
                PASSWORD
        );
    }
}