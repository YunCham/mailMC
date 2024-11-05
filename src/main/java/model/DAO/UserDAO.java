package model.DAO;

import dbConnection.SQLDatabaseManager;
import model.UserModel;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
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

//    public boolean createUser(UserModel user) throws SQLException {
//        boolean createOK = false;
//
//        try {
//            initDBConnection();
//
//            if (emailExists(user.getEmail())) {
//                System.out.println("El correo ya está registrado.");
//                return false;
//            }
//
//            if (phoneExists(user.getPhone())) {
//                System.out.println("El número de teléfono ya está registrado.");
//                return false;
//            }
//
//            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
//
//
//            String sql = "INSERT INTO users (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
//
//            PreparedStatement statement = connection.prepareStatement(sql);
//
//            statement.setString(1, user.getName());      // Asigna el nombre
//            statement.setString(2, user.getEmail());     // Asigna el email
//            statement.setString(3, hashedPassword);      // Asigna la contraseña
//            statement.setString(4, user.getPhone());     // Asigna el teléfono
//            statement.setString(5, user.getAddress());   // Asigna la dirección
//
//
//            int rowsAffected = statement.executeUpdate();
//
//            if (rowsAffected > 0) {
//                createOK = true;
//                System.out.println("Usuario creado correctamente.");
//            } else {
//                throw new SQLException("No se pudo crear el usuario.");
//            }
//
//        } catch (SQLException e) {
//            throw new SQLException("Error al crear el usuario: " + e.getMessage(), e);
//        } finally {
//            closeDBConnection();
//        }
//
//        return createOK;
//    }

    public String createUser(UserModel user) throws SQLException {
        String resultMessage;

        try {
            initDBConnection();

            // Validar campos no nulos o vacíos
            if (user.getName() == null || user.getEmail() == null || user.getPassword() == null ||
                    user.getPhone() == null || user.getAddress() == null) {
                return "Todos los campos son obligatorios.";
            }

            if (emailExists(user.getEmail())) {
                return "El correo ya está registrado.";
            }

            if (phoneExists(user.getPhone())) {
                return "El número de teléfono ya está registrado.";
            }

            String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

            String sql = "INSERT INTO users (name, email, password, phone, address) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, user.getName());      // Asigna el nombre
            statement.setString(2, user.getEmail());     // Asigna el email
            statement.setString(3, hashedPassword);      // Asigna la contraseña
            statement.setString(4, user.getPhone());     // Asigna el teléfono
            statement.setString(5, user.getAddress());   // Asigna la dirección

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                resultMessage = "Usuario creado correctamente.";
            } else {
                throw new SQLException("No se pudo crear el usuario.");
            }

        } catch (SQLException e) {
            throw new SQLException("Error al crear el usuario: " + e.getMessage(), e);
        } finally {
            closeDBConnection();
        }

        return resultMessage;
    }

    public String updateUser(UserModel user) throws SQLException {
        String resultMessage;

        try {
            initDBConnection();

            String sql = "UPDATE users SET name = ?, email = ?, password = ?, phone = ?, address = ? WHERE email = ?";
            PreparedStatement statement = connection.prepareStatement(sql);

            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
            statement.setString(4, user.getPhone());
            statement.setString(5, user.getAddress());
//            statement.setInt(6, user.getId());
            statement.setString(6, user.getEmail());

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                resultMessage = "Usuario actualizado correctamente.";
            } else {
                resultMessage = "No se encontró el usuario para actualizar.";
            }
        } catch (SQLException e) {
            throw new SQLException("Error al actualizar el usuario: " + e.getMessage(), e);
        } finally {
            closeDBConnection();
        }

        return resultMessage;
    }

    public String deleteUser(String email) throws SQLException {
        String resultMessage;

        try {
            initDBConnection();

//          String sql = "UPDATE users SET status = 0 WHERE email = ?";
            String sql = "UPDATE users SET status = CASE WHEN status = '1' THEN '0' ELSE '1' END WHERE email = ?";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                resultMessage = "Usuario eliminado correctamente.";
            } else {
                resultMessage = "No se encontró el usuario para eliminar.";
            }
        } catch (SQLException e) {
            throw new SQLException("Error al eliminar el usuario: " + e.getMessage(), e);
        } finally {
            closeDBConnection();
        }

        return resultMessage;
    }


    public List<UserModel> listAllUsers(String param) throws SQLException {
        List<UserModel> users = new ArrayList<>();

        try {
            initDBConnection();

            String query;

            if ("*".equals(param)) {
                query = "SELECT * FROM users";
            } else {
                query = "SELECT * FROM users WHERE email LIKE ? OR phone LIKE ? OR name LIKE ?";
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
                UserModel user = new UserModel();
                user.setId(resultSet.getInt("id"));
                user.setName(resultSet.getString("name"));
                user.setEmail(resultSet.getString("email"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setRole(resultSet.getString("role"));
                user.setStatus(resultSet.getString("status"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new SQLException("Error al listar los usuarios: " + e.getMessage(), e);
        } finally {
            closeDBConnection();
        }

        return users;
    }

    public boolean userLogin(UserModel user) throws  SQLException{
        boolean loginSuccess = false;
        String query = "SELECT password FROM users WHERE email = ? AND status = '1'";

        try {
            initDBConnection();

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, user.getEmail());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                String hashedPassword = resultSet.getString("password");

                if (BCrypt.checkpw(user.getPassword(), hashedPassword)) {
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
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, email);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
        return false;
    }

    private boolean phoneExists(String phone) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, phone);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return resultSet.getInt(1) > 0;
        }
        return false;
    }
}
