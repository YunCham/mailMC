package communication;

import utils.Config;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientePOP {
 
    public ClientePOP() {
    }

    public static String readMail() {
        String result = null;
        BufferedReader reader;
        DataOutputStream writer;
        String command;

        try {
            // Estableciendo Conexion Socket
            Socket socket = new Socket(Config.getMailSmtpHost(), Config.getMailSmtpPort());
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new DataOutputStream(socket.getOutputStream());

            System.out.println("Entro");
            reader.readLine();
            command = "USER " + Config.getMailSmtpUser() + "\r\n";

            writer.writeBytes(command);
            reader.readLine();
            System.out.println(command);
            command = "PASS " + Config.getMailSmtpPassword() + "\r\n";

            writer.writeBytes(command);
            reader.readLine();
            System.out.println(command);
            command = "LIST \r\n";

            writer.writeBytes(command);
            System.out.println(command);
            char cantidad = reader.readLine().charAt(4);
            getMultiline(reader);

            if (cantidad != '0') {
                // Leer mensaje
                command = "RETR 1\n";
                writer.writeBytes(command);
                result = getMultiline(reader);
                System.out.println(command);
                System.out.println(result);

                // Eliminar mensaje despues de leer
                command = "DELE 1\n";
                writer.writeBytes(command);
                reader.readLine();
                System.out.println(command);
            }

            command = "QUIT\r\n";
            writer.writeBytes(command);
            reader.readLine();


            writer.close();
            reader.close();
            socket.close();
        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return result;
    }

    static protected String getMultiline(BufferedReader in) throws IOException {
//        String lines = "";
//        while (true) {
//            String line = in.readLine();
//            if (line == null) {
//                // Server closed connection
//                throw new IOException(" S : Server unawares closed the connection.");
//            }
//            if (line.equals(".")) {
//                // No more lines in the server response
//                break;
//            }
//            if ((line.length() > 0) && (line.charAt(0) == '.')) {
//                // The line starts with a "." - strip it off.
//                line = line.substring(1);
//            }
//            // Add read line to the list of lines
//            lines = lines + "\n" + line;
//        }
//        return lines;

        StringBuilder lines = new StringBuilder();
        String returnPath = null;
        String subject = null;

        while (true) {
            String line = in.readLine();

            if (line == null) {
                throw new IOException("S : Server unawares closed the connection.");
            }

            if (line.equals(".")) {
                break;
            }

            // Quitar el punto inicial si la línea comienza con '.'
            if (line.startsWith(".")) {
                line = line.substring(1);
            }

            if (line.startsWith("Return-Path:")) {
                returnPath = line;
            } else if (line.startsWith("Subject:")) {
                subject = line;
            }

            // Agregar la línea al mensaje completo
            lines.append(line).append("\n");
        }

        // Imprimir Return-Path y Subject, si están presentes
        if (returnPath != null) {
            System.out.println(returnPath);
        } else {
            System.out.println("Return-Path no encontrado.");
        }

        if (subject != null) {
            System.out.println( subject);
        } else {
            System.out.println("Subject no encontrado.");
        }

        return lines.toString();

    }
}
