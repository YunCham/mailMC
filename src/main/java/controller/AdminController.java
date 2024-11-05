package controller;

import model.AdminModel;
import model.DAO.AdminDAO;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;

public class AdminController {

    private AdminDAO adminDAO;
    private String result;

    public AdminController(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
        this.result = "Operación no realizada.";
    }

    /**
     * Realiza una operación en función del tipo de operación especificado.
     *
     * @param operation El tipo de operación a realizar (INSERT, SELECT, UPDATE, DELETE).
     * @param admin    Los valores necesarios para realizar la operación.
     * @throws SQLException Si ocurre un error al ejecutar la operación en la base de datos.
     */
    public void operationSet(String operation, AdminModel admin)  {
//         result = "Operación no realizada.";

        switch (operation){
            case "INSERT":
                try {
                    this.result = adminDAO.createAdmin(admin);
                } catch (SQLException e) {
                    System.err.println("Error al insertar el usuario: " + e.getMessage());
                    this.result = "Error al insertar el usuario.";
                }
                break;

            case "SELECT":

                try {
                    String param = admin.getEmail() != null ? admin.getEmail() :
                            admin.getName() != null ? admin.getName() :
                                    admin.getPhone() != null ? admin.getPhone() : "*";

                    List<AdminModel> admins = adminDAO.listAllAdmins(param);

                    // Aquí manejar los resultados, por ejemplo, imprimir o mostrar en la UI
                    if (admins.isEmpty()) {
                        this.result = "No se encontraron admin.";
                    } else {
                        this.result = "Usuarios encontrados: " + admins.size();
                        // Ejemplo de cómo podrías imprimir los usuarios
                        for (AdminModel u : admins) {
                            System.out.println("ID: " + u.getId() + ", Nombre: " + u.getName() + ", Email: " + u.getEmail() + ",Role:" + u.getRole());
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Error al seleccionar los Admin: " + e.getMessage());
                    this.result = "Error al seleccionar los Admin.";
                }

                break;

            case "UPDATE":
                try {
                    this.result = adminDAO.updateAdmin(admin);
                } catch (SQLException e) {
                    System.err.println("Error al actualizar el Admin: " + e.getMessage());
                    this.result = "Error al actualizar el Admin.";
                }
                break;

            case "DELETE":
                try {
                    this.result = adminDAO.deleteAdmin(admin.getEmail());
                } catch (SQLException e) {
                    System.err.println("Error al actualizar el Admin: " + e.getMessage());
                    this.result = "Error al actualizar el Admin.";
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
    public String adminLogin(AdminModel admin) {
//        String resultMessage;

        try {
            boolean loginSuccess = adminDAO.adminLogin(admin);

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
