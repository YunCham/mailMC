package communication;

import utils.Config;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class POP {
    public static String remitente = "";

//    String servidor="mail.tecnoweb.org.bo";
    //String servidor="172.20.172.254";
static String comando="";
    String linea="";
    int puerto=110;
    static String[] subject = null;
    static String retorno = "";

    public static String recogerInfo() throws IOException {
        try{
            Socket socket=new Socket(Config.getMailSmtpHost(),Config.getMailSmtpPort());
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

            comando="USER "+ Config.getMailSmtpUser() + "\r\n";
            System.out.print("C : " + comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine()+"\r\n");

            comando= "PASS " + Config.getMailSmtpPassword() + "\r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine()+"\r\n");

            comando="LIST \r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : " +getMultiline(entrada)+ "\r\n");

            comando = "STAT \r\n";
            System.out.print(comando);
            salida.writeBytes( comando );
            String stat = entrada.readLine();
            String[] array = stat.split(" ");
            String numero = array[1];

            comando="RETR " + numero + "\r\n";
            //comando="RETR Ultimo correo\r\n";     Esto es para el caso de correo real
            System.out.print("C : "+comando);
            salida.writeBytes( comando );

            String[] arraySub = getMultiline(entrada).split("\n");

            remitente = arraySub[39];  // Capturar el remitente
            remitente = limpiarRemitente(remitente);
            System.out.printf(remitente);

            Boolean bandera = true;
            int indice = 38;   //42

            while (bandera){
                System.out.println(arraySub[indice] + "\r\n");

//                if (arraySub[indice].startsWith("From:")) {
//                    remitente = arraySub[indice];
//                }

                String emailTo = "To: "+ Config.getMailSmtpUsername();
                if (emailTo.equals(arraySub[indice])){
                    bandera = false;
                }else{
                    if (indice == 38){
                        subject = arraySub[indice].split(" ");
                        retorno = "";
                        for(int i=1;i<subject.length;i++){
                            if (i == 1){
                                retorno = retorno + subject[i];
                            }   else{
                                retorno = retorno + " " + subject[i];
                            }
                        }
                    }else{
                        retorno = retorno + arraySub[indice];
                    }
                }
                indice++;
            }
            retorno = remitente + "-" + limpiarSubject(arraySub[42]);                        //   modificacion hecha 42
            System.out.printf(retorno + "\r\n");
            comando="QUIT\r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine()+"\r\n");

            salida.close();
            entrada.close();
            socket.close();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error al conectar" + e);
        }
        return retorno;
    }

    static protected String getMultiline(BufferedReader in) throws IOException{
        String lines = "";
        while (true){
            String line = in.readLine();
            if (line == null){
                // Server closed connection
                throw new IOException(" S : Server unawares closed the connection.");
            }
            if (line.equals(".")){
                // No more lines in the server response
                break;
            }
            if ((line.length() > 0) && (line.charAt(0) == '.')){
                // The line starts with a "." - strip it off.
                line = line.substring(1);
            }
            // Add read line to the list of lines
            lines=lines+"\n"+line;
        }
        return lines;
    }



    public String obtenerDestinatario() throws IOException{
        try{
            Socket socket=new Socket(Config.getMailSmtpHost(),Config.getMailSmtpPort());
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream salida = new DataOutputStream(socket.getOutputStream());

            comando="USER grupo06sa\r\n";
            //+usuario+"\r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine()+"\r\n");

            comando="PASS grup006grup006\r\n";
            //+contraseña+"\r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine()+"\r\n");

            comando="LIST \r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : " +getMultiline(entrada)+ "\r\n");

            comando = "STAT \r\n";
            System.out.print(comando);
            salida.writeBytes( comando );
            String stat = entrada.readLine();
            String[] array = stat.split(" ");
            String numero = array[1];

            comando="RETR " + numero + "\r\n";
            //comando="RETR Ultimo correo\r\n";     Esto es para el caso de correo real
            System.out.print("C : "+comando);
            salida.writeBytes( comando );

            String[] arraySub = getMultiline(entrada).split("\n");


            Boolean bandera = true;

            int indice = 38;
            while (bandera){
                System.out.println(arraySub[indice] + "\r\n");

//                if (arraySub[indice].startsWith("From:")) {
//                    remitente = arraySub[indice];
//                }

                if ("To: grupo13sa@tecnoweb.org.bo".equals(arraySub[indice])){
                    bandera = false;
                }else{
                    if (indice == 38){
                        subject = arraySub[indice].split(" ");
                        retorno = "";
                        for(int i=1;i<subject.length;i++){
                            if (i == 1){
                                retorno = retorno + subject[i];
                            }   else{
                                retorno = retorno + " " + subject[i];
                            }
                        }
                    }else{
                        retorno = retorno + arraySub[indice];
                    }
                }
                indice++;
            }
            remitente = arraySub[39];  // Capturar el remitente
            remitente = limpiarRemitente(remitente);

            retorno = remitente;                        //   modificacion hecha

            comando="QUIT\r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine()+"\r\n");

            salida.close();
            entrada.close();
            socket.close();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "Error al conectar" + e);
        }
        return retorno;
    }

    //Metodo par limpiar remitente
    private static String limpiarRemitente(String remitente) {
        int start = remitente.indexOf("<");
        int end = remitente.indexOf(">");
        if (start != -1 && end != -1) {
            return remitente.substring(start + 1, end);
        }
        return remitente;
    }

    public static String limpiarSubject(String subjectLine) {
        if (subjectLine.startsWith("Subject:")) {
            return subjectLine.replaceFirst("Subject:", "").trim();
        }
        return subjectLine;
    }
}