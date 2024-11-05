package controller;

import communication.Email;
import communication.EmailSend;
import model.DAO.UserDAO;
import model.UserModel;
import utils.ClearToken;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class UserController {

    private UserDAO userDAO;
    private String result;

    private Email email;
    private EmailSend emailSend;
    private ClearToken clearToken;

    public UserController(UserDAO userDAO) {
        this.userDAO = userDAO;
        this.email = new Email();
        this.result = "Operación no realizada.";
    }

    /**
     * Realiza una operación en función del tipo de operación especificado.
     *
     * @param operation El tipo de operación a realizar (INSERT, SELECT, UPDATE, DELETE).
     * @param user    Los valores necesarios para realizar la operación.
     * @throws SQLException Si ocurre un error al ejecutar la operación en la base de datos.
     */
    public void operationSet(String remitente, String operation, UserModel user)  {
//         result = "Operación no realizada.";

         email.setTo(remitente);
         email.setSubject(operation);

        switch (operation){
            case "INSERT":
                try {
                    this.result = userDAO.createUser(user);
                     email.setMessage(this.result);
                } catch (SQLException e) {
                    System.err.println("Error al insertar el usuario: " + e.getMessage());
                    this.result = "Error al insertar el usuario.";
                    email.setMessage(this.result);
                }
                break;

            case "SELECT":

                try {
                    String param = user.getEmail() != null ? user.getEmail() :
                            user.getName() != null ? user.getName() :
                                    user.getPhone() != null ? user.getPhone() : "*";

                    List<UserModel> users = userDAO.listAllUsers(param);

                    // Aquí manejar los resultados, por ejemplo, imprimir o mostrar en la UI
                    if (users.isEmpty()) {
                        this.result = "No se encontraron usuarios.";
                        email.setMessage(this.result);

                    } else {
                        this.result = "Usuarios encontrados: " + users.size();

                         String valueTable="";
                        // Ejemplo de cómo podrías imprimir los usuarios
                        for (UserModel u : users) {

                            valueTable= "ID: " + u.getId() + ", Nombre: " + u.getName() + ", Email: " + u.getEmail() + ",Role:" + u.getRole() + "\r\n";

                            System.out.println("ID: " + u.getId() + ", Nombre: " + u.getName() + ", Email: " + u.getEmail() + ",Role:" + u.getRole());
                        }

                        this.result = this.result + "\r\n" + valueTable;
                        email.setMessage(this.result);
                    }
                } catch (SQLException e) {
                    System.err.println("Error al seleccionar los usuarios: " + e.getMessage());
                    this.result = "Error al seleccionar los usuarios.";
                }

                break;

            case "UPDATE":
                try {
                    this.result = userDAO.updateUser(user);
                    email.setMessage(this.result);
                } catch (SQLException e) {
                    System.err.println("Error al actualizar el usuario: " + e.getMessage());
                    this.result = "Error al actualizar el usuario.";
                    email.setMessage(this.result);
                }
                break;

            case "DELETE":
                try {
                    this.result = userDAO.deleteUser(user.getEmail());
                    email.setMessage(this.result);
                } catch (SQLException e) {
                    System.err.println("Error al actualizar el usuario: " + e.getMessage());
                    this.result = "Error al actualizar el usuario.";
                    email.setMessage(this.result);
                }
                break;

            default:
                email.setMessage("Operación no soportada: " + operation);
                throw new IllegalArgumentException("Operación no soportada: " + operation);
        }

        EmailSend emailSender = new EmailSend(email);
        Thread thread = new Thread(emailSender);
        thread.start();

        // Mostrar el JOptionPane y cerrarlo después de 2 segundos
        final JOptionPane optionPane = new JOptionPane(this.result, JOptionPane.INFORMATION_MESSAGE);
        final JDialog dialog = optionPane.createDialog("Estado de la Operacion");

        Timer timer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        timer.setRepeats(false);
        timer.start();

        dialog.setVisible(true);
    }

    // Metodo Login
    public String userLogin(String remitente, UserModel user) {
//        String resultMessage;

        try {
            boolean loginSuccess = userDAO.userLogin(user);

            if (loginSuccess) {
                this.result = "Inicio de sesión exitoso";
                System.out.println(this.result);
            } else {
                this.result = "Email o contraseña incorrectos";
                System.out.println(this.result);

                Email email = new Email(remitente, "Error", this.result);

                // Crear y ejecutar el hilo para enviar el correo
                EmailSend emailSender = new EmailSend(email);
                Thread thread = new Thread(emailSender);
                thread.start();
            }
        } catch (SQLException e) {
            this.result = "Error al intentar iniciar sesión: " + e.getMessage();

            Email email = new Email(remitente, "Error:", this.result);

            // Crear y ejecutar el hilo para enviar el correo
            EmailSend emailSender = new EmailSend(email);
            Thread thread = new Thread(emailSender);
            thread.start();

            e.printStackTrace();
        }

        return this.result;
    }



}
