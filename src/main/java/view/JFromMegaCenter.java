package view;

import javax.swing.*;

import communication.ClientePOP;
import communication.POP;
import utils.ClearToken;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class JFromMegaCenter extends JFrame{

    private JButton BTN_connection;
    private JButton BTN_closeConnection;
    private JLabel LBL_result;
    private JPanel panel1;

    private HiloCorreo myThread;

    public JFromMegaCenter(){
        setContentPane(panel1);
        setLocationRelativeTo(null);
        setSize(600,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

        BTN_connection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendConnecTo();
            }
        });

        BTN_closeConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                disconnectTo();
            }
        });

    }

    //Metodo para comenzar iniciar
    public void sendConnecTo(){

        try {

            myThread = new HiloCorreo();
            myThread.start();

            BTN_closeConnection.setEnabled(true);
            BTN_connection.setEnabled(false);

            LBL_result.setText("Parámetros de BD enviados, hilo de correo iniciado....");

        } catch (Exception e) {
            LBL_result.setText("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void disconnectTo(){
        try {
            if (myThread != null) {
                myThread.estado = false;
            }

            BTN_closeConnection.setEnabled(false);
            BTN_connection.setEnabled(true);

            LBL_result.setText("Hilo detenido y conexión cerrada exitosamente.");

        } catch (Exception e) {
            LBL_result.setText("Error al desconectar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //    Hilo que realiza el trabajo de procesamiento de correos
    public class HiloCorreo extends Thread {

        public volatile boolean estado = true;

        @Override
        public void run() {
            LBL_result.setText("");
            System.out.println("Iniciando listener de correos...");
            LBL_result.setText("Iniciando listener de correos...");
            while (estado) {

//                String content = ClientePOP.readMail();
                String content = null;
                try {
                    content = POP.recogerInfo();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (content != null) {
                    System.out.println("Nuevo correo recibido: " + content);
                    LBL_result.setText("Nuevo correo recibido: " + content);
                    new HiloAtencion(content).start();
                }
                waitCiclo();
            }
            System.out.println("Fin del listener de correos.");
            LBL_result.setText("Fin del listener de correos.");
        }

        public void waitCiclo() {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // Hilo para procesar el contenido de los correos recibidos
    public class HiloAtencion extends Thread {

        private final String mensaje;

        public HiloAtencion(String mensaje) {
            this.mensaje = mensaje;
        }

        @Override
        public void run() {
            System.out.println("Procesando mensaje: " + mensaje);
            LBL_result.setText("Procesando mensaje: " + mensaje);

            // Lógica para procesar el mensaje aquí

            ClearToken clearToken = new ClearToken(mensaje);
            if (clearToken.validateAndProcessToken()) {
                System.out.println("Token procesado exitosamente.");
                LBL_result.setText("Token procesado exitosamente.");
            } else {
                System.out.println("Error en el formato del token.");
                LBL_result.setText("Error en el formato del token.");
            }


        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new JFromMegaCenter();
            }
        });
    }
}
