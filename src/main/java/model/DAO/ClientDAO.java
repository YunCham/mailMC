package model.DAO;

import dbConnection.SQLDatabaseManager;
import model.ClientModel;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientDAO {

    private Connection connection;

    private  boolean initDBConnection(){
        try {
            connection = SQLDatabaseManager.getConnection();
            return  true;
        } catch (SQLException e){
            System.err.println("Error: al connectar a la BD");
        }
        return false;
    }

    private boolean closeDBConnection(){
        try {
            SQLDatabaseManager.closeConnection(connection);
            return true;
        } catch (SQLException e){
            System.err.println("Error: al desconectar la BD"+ e.getMessage());
        }
        return false;
    }

    public String createClient(ClientModel client) throws SQLException {
        String resultMessage;

        try {
            initDBConnection();

            // Validar campos no nulos o vacíos
            if (client.getName() == null || client.getEmail() == null || client.getPassword() == null ||
                    client.getPhone() == null || client.getAddress() == null) {
                return "Todos los campos son obligatorios.";
            }

            if (emailExists(client.getEmail())) {
                return "El correo ya está registrado.";
            }

            if (phoneExists(client.getPhone())) {
                return "El número de teléfono ya está registrado.";
            }

            String hashedPassword = BCrypt.hashpw(client.getPassword(), BCrypt.gensalt());

            String sql = "INSERT INTO clients (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, client.getName());      // Asigna el nombre
            statement.setString(2, client.getEmail());     // Asigna el email
            statement.setString(3, hashedPassword);      // Asigna la contraseña
            statement.setString(4, client.getPhone());     // Asigna el teléfono
            statement.setString(5, client.getAddress());   // Asigna la dirección

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                resultMessage = "Admin creado correctamente.";
            } else {
                throw new SQLException("No se pudo crear el Client.");
            }

        } catch (SQLException e) {
            throw new SQLException("Error al crear el client: " + e.getMessage(), e);
        } finally {
            closeDBConnection();
        }

        return resultMessage;
    }

    public String updateClient(ClientModel client) throws SQLException {
        String resultMessage;

        try {
            initDBConnection();

            String sql = "UPDATE clients SET name = ?, email = ?, password = ?, phone = ?, address = ? WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, client.getName());
            statement.setString(2, client.getEmail());
            statement.setString(3, BCrypt.hashpw(client.getPassword(), BCrypt.gensalt()));
            statement.setString(4, client.getPhone());
            statement.setString(5, client.getAddress());
//            statement.setInt(6, client.getId());
            statement.setString(6, client.getEmail());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                resultMessage = "Admin actualizado correctamente.";
            } else {
                resultMessage = "No se encontró el usuario para client.";
            }
        } catch (SQLException e) {
            throw new SQLException("Error al actualizar el client: " + e.getMessage(), e);
        } finally {
            closeDBConnection();
        }

        return resultMessage;
    }

    public String deleteClient(String email) throws SQLException {
        String resultMessage;

        try {
            initDBConnection();

//          String sql = "UPDATE users SET status = 0 WHERE email = ?";
            String sql = "UPDATE clients SET status = CASE WHEN status = '1' THEN '0' ELSE '1' END WHERE email = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                resultMessage = "Admin eliminado correctamente.";
            } else {
                resultMessage = "No se encontró el admin para eliminar.";
            }
        } catch (SQLException e) {
            throw new SQLException("Error al eliminar el admin: " + e.getMessage(), e);
        } finally {
            closeDBConnection();
        }

        return resultMessage;
    }


    public List<ClientModel> listAllClients(String param) throws SQLException {
        List<ClientModel> clients = new ArrayList<>();

        try {
            initDBConnection();

            String query;

            if ("*".equals(param)) {
                query = "SELECT * FROM clients";
            } else {
                query = "SELECT * FROM clients WHERE email LIKE ? OR phone LIKE ? OR name LIKE ?";
            }

            PreparedStatement statement = connection.prepareStatement(query);

            if (!"*".equals(param)) {
                String likeParam = "%" + param + "%";
                statement.setString(1, likeParam);
                statement.setString(2, likeParam);
                statement.setString(3, likeParam);
            }

            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ClientModel client = new ClientModel();
                client.setId(resultSet.getInt("id"));
                client.setName(resultSet.getString("name"));
                client.setEmail(resultSet.getString("email"));
                client.setPhone(resultSet.getString("phone"));
                client.setAddress(resultSet.getString("address"));
                client.setRole(resultSet.getString("role"));
                client.setStatus(resultSet.getString("status"));
                clients.add(client);
            }
        } catch (SQLException e) {
            throw new SQLException("Error al listar los admin: " + e.getMessage(), e);
        } finally {
            closeDBConnection();
        }

        return clients;
    }

    public boolean clientLogin(ClientModel client) throws  SQLException{
        boolean loginSuccess = false;
        String query = "SELECT password FROM clients WHERE email = ? AND status = '1'";

        try {
            initDBConnection();

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, client.getEmail());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password");

                if (BCrypt.checkpw(client.getPassword(), hashedPassword)) {
                    loginSuccess = true;
                }
            }
        } catch (SQLException e) {
            throw new SQLException("Error al ejecutar el login: " + e.getMessage(), e);
        } finally {
            closeDBConnection();
        }

        return loginSuccess;
    }


    private boolean emailExists(String email) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clients WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, email);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
        return false;
    }

    private boolean phoneExists(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM clients WHERE phone = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, phone);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
        return false;
    }
}
