package controller;

import model.AdminModel;
import model.ClientModel;
import model.DAO.AdminDAO;
import model.DAO.ClientDAO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class ClientController {


    private ClientDAO clientDAO;
    private String result;

    public ClientController(ClientDAO clientDAO) {
        this.clientDAO = clientDAO;
        this.result = "Operación no realizada.";
    }

    /**
     * Realiza una operación en función del tipo de operación especificado.
     *
     * @param operation El tipo de operación a realizar (INSERT, SELECT, UPDATE, DELETE).
     * @param client    Los valores necesarios para realizar la operación.
     * @throws SQLException Si ocurre un error al ejecutar la operación en la base de datos.
     */
    public void operationSet(String operation, ClientModel client)  {
//         result = "Operación no realizada.";

        switch (operation){
            case "INSERT":
                try {
                    this.result = clientDAO.createClient(client);
                } catch (SQLException e) {
                    System.err.println("Error al insertar el client: " + e.getMessage());
                    this.result = "Error al insertar el client.";
                }
                break;

            case "SELECT":

                try {
                    String param = client.getEmail() != null ? client.getEmail() :
                            client.getName() != null ? client.getName() :
                                    client.getPhone() != null ? client.getPhone() : "*";

                    List<ClientModel> users = clientDAO.listAllClients(param);

                    // Aquí manejar los resultados, por ejemplo, imprimir o mostrar en la UI
                    if (users.isEmpty()) {
                        this.result = "No se encontraron client.";
                    } else {
                        this.result = "client encontrados: " + users.size();
                        // Ejemplo de cómo podrías imprimir los usuarios
                        for (ClientModel u : users) {
                            System.out.println("ID: " + u.getId() + ", Nombre: " + u.getName() + ", Email: " + u.getEmail() + ",Role:" + u.getRole());
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error al seleccionar los client: " + e.getMessage());
                    this.result = "Error al seleccionar los client.";
                }

                break;

            case "UPDATE":
                try {
                    this.result = clientDAO.updateClient(client);
                } catch (SQLException e) {
                    System.err.println("Error al actualizar el client: " + e.getMessage());
                    this.result = "Error al actualizar el client.";
                }
                break;

            case "DELETE":
                try {
                    this.result = clientDAO.deleteClient(client.getEmail());
                } catch (SQLException e) {
                    System.err.println("Error al actualizar el client: " + e.getMessage());
                    this.result = "Error al actualizar el client.";
                }
                break;

            default:
                throw new IllegalArgumentException("Operación no soportada: " + operation);
        }

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
    public String clientLogin(ClientModel client) {
//        String resultMessage;

        try {
            boolean loginSuccess = clientDAO.clientLogin(client);

            if (loginSuccess) {
                this.result = "Inicio de sesión exitoso";
                System.out.println(this.result);
            } else {
                this.result = "Email o contraseña incorrectos";
                System.out.println(this.result);
            }
        } catch (SQLException e) {
            this.result = "Error al intentar iniciar sesión: " + e.getMessage();
            e.printStackTrace();
        }

        return this.result;
    }

}
