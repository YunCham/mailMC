package Presentation;

import Communication.ClientePOP;
import Date.dbConnection.SQLDatabaseManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

//import static com.sun.org.apache.xerces.internal.util.DOMUtil.setVisible;

public class megaCenterJFrom  extends JFrame {
    private JPanel panel1;
    private JTextField TXF_urlBD;
    private JTextField TXF_userBD;
    private JTextField TXF_passBD;
    private JButton BTN_closeConnection;
    private JButton BTN_connection;
    private JLabel LBL_result;

    private Connection dbConnection;
    private HiloCorreo myThread;

    public megaCenterJFrom() {
        setTitle("MegaCenter - Conexión BD y Correo");
        setContentPane(panel1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        BTN_connection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToDatabase();
            }
        });

        BTN_closeConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectFromDatabase();
            }
        });
    }

    /**
     * Método para conectar a la base de datos y empezar el hilo de correo
     */
    private void connectToDatabase() {
        String url = TXF_urlBD.getText();
        String user = TXF_userBD.getText();
        String password = TXF_passBD.getText();

        try {
            // Intentar conectar a la base de datos
            dbConnection = SQLDatabaseManager.getConnection(url, user, password);
            if (dbConnection != null) {
                LBL_result.setText("Conexión exitosa a la BD.");
                // Iniciar el hilo que se encargará de la lógica de correo
                myThread = new HiloCorreo();
                myThread.start();
                BTN_closeConnection.setEnabled(true);
                BTN_connection.setEnabled(false);
            }
        } catch (SQLException e) {
            LBL_result.setText("Error al conectar: " + e.getMessage());
        }
    }

    /**
     * Método para desconectar de la base de datos y detener el hilo de correo
     */
    private void disconnectFromDatabase() {
        if (myThread != null) {
            myThread.estado = false; // Detener el hilo
        }

        try {
            // Cerrar la conexión a la base de datos
            if (dbConnection != null && !dbConnection.isClosed()) {
                SQLDatabaseManager.closeConnection(dbConnection);
                LBL_result.setText("Conexión cerrada.");
            }
        } catch (SQLException e) {
            LBL_result.setText("Error al cerrar la conexión: " + e.getMessage());
        }

        // Rehabilitar el botón de conexión y deshabilitar el de desconexión
        BTN_closeConnection.setEnabled(false);
        BTN_connection.setEnabled(true);
    }

    /**
     * Hilo que realiza el trabajo de procesamiento de correos
     */
    public class HiloCorreo extends Thread {

        public volatile boolean estado = true;

        @Override
        public void run() {
            System.out.println("Iniciando listener de correos...");
            while (estado) {
                // Aquí puedes hacer la llamada al servidor POP o cualquier lógica que necesites
                String content = ClientePOP.readMail(); // ClientePOP sería una clase que has implementado para leer correos
                if (content != null) {
                    System.out.println("Nuevo correo recibido: " + content);
                    new HiloAtencion(content).start(); // Procesar el contenido del correo
                }
                waitCiclo();
            }
            System.out.println("Fin del listener de correos.");
        }

        public void waitCiclo() {
            try {
                Thread.sleep(2000); // Esperar 2 segundos antes de volver a preguntar por correos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Hilo para procesar el contenido de los correos recibidos
     */
    public class HiloAtencion extends Thread {

        private final String mensaje;

        public HiloAtencion(String mensaje) {
            this.mensaje = mensaje;
        }

        @Override
        public void run() {
            System.out.println("Procesando mensaje: " + mensaje);
            // Lógica para procesar el mensaje aquí
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new megaCenterJFrom();
            }
        });
    }


}
