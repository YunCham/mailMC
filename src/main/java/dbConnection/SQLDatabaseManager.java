package dbConnection;

import utils.Config;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLDatabaseManager {
    // Credenciales para la conexion a la base de datos  de POSTGRESQL
    //private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/tiendadb";
    private static final String JDBC_URL = Config.getDbUrl();
    private static final String USER     = Config.getDbUsername();
    private static final String PASSWORD = Config.getDbPassword();

    /**
     * Establece una conexioni a la base de  datos PostgreSQL
     *
     * @return una referencia a la conexion de la base de datos
     * @throws SQLException si ocurre un error durante la conexion,
     */
    public static Connection getConnection() throws SQLException {
        Connection connection = null;

        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(JDBC_URL,USER,PASSWORD);

            if (connection == null) {
                throw new SQLException("Error: Failed to connect to the database");
            }

            // Mostrar el JOptionPane y cerrarlo después de 1 segundos
            final JOptionPane optionPane = new JOptionPane(connection, JOptionPane.INFORMATION_MESSAGE);
            final JDialog dialog = optionPane.createDialog("Conexión exitosa");

            Timer timer = new Timer(1000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dialog.dispose();
                }
            });
            timer.setRepeats(false);
            timer.start();

            dialog.setVisible(true);

            System.out.println(connection);

        } catch (ClassNotFoundException e) {
            throw new SQLException("Error loading database driver");
        } catch (SQLException e) {
            throw new SQLException("Error connecting to the database: " + e.getMessage());
        }
        return connection;
    }

//    public static Connection getConnection(String url, String user, String password) throws SQLException {
//        try {
//            Class.forName("org.postgresql.Driver");
//            System.out.printf("Conexión exitosa a la BD.\n");
//            return DriverManager.getConnection(url, user, password);
//        } catch (ClassNotFoundException e) {
//            throw new SQLException("Error al cargar el driver de la base de datos.", e);
//        }
//    }


    /**
     * Cierra la conexion a la base de datos
     * @param connection la conexion que se debe cerras
     * @throws SQLException si ocurre un error durante la desconexion
     *
     */
    public static void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
