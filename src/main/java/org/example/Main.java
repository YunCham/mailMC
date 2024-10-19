package org.example;

import Date.dbConnection.SQLDatabaseManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {

        Connection connection = null;

        try {
            // Intenta establecer la conexión
            connection = SQLDatabaseManager.getConnection();
            System.out.println("Conexión establecida con éxito.");

        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            // Cerrar la conexión
            try {
                SQLDatabaseManager.closeConnection(connection);
                System.out.println("Conexión cerrada con éxito.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
}