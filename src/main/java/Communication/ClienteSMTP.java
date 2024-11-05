package communication;

import utils.Config;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClienteSMTP {

    String comando="";

    public void enviarCorreo(String patron,String mssg){
        try{
            String user_receptor = Config.getMailSmtpUsername();

            //se establece conexion abriendo un socket especificando el servidor y puerto SMTP
            Socket socket=new Socket(Config.getMailSmtpHost(),Config.getMailPopPort());
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            DataOutputStream salida = new DataOutputStream (socket.getOutputStream());

            // Escribimos datos en el canal de salida establecido con el puerto del protocolo SMTP del servidor
            System.out.println("S : " + entrada.readLine());

            comando="MAIL FROM : "+Config.getMailSmtpUsername()+" \r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine());

            comando="RCPT TO : "+user_receptor+" \r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine());

            comando="DATA\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+getMultiline(entrada));

            String subject = patron;
            String mensaje = mssg;
            comando="SUBJECT : " + subject + "\r\n" + mensaje + "\n" + ".\r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine());

            comando="QUIT\r\n";
            System.out.print("C : "+comando);
            salida.writeBytes( comando );
            System.out.println("S : "+entrada.readLine());
            // Cerramos los flujos de salida y de entrada y el socket cliente
            salida.close();
            entrada.close();
            socket.close();
        }catch(UnknownHostException e){
            System.out.println(" S : No se pudo conectar con el servidor indicado");
        }catch (IOException e){
        }
    }

    static protected String getMultiline(BufferedReader in) throws IOException{
        String lines = "";
        while (true){
            String line = in.readLine();
            if (line == null){
                // Server closed connection
                throw new IOException(" S : Server unawares closed the connection.");
            }
            if (line.charAt(3)==' '){
                lines=lines+"\n"+line;
                // No more lines in the server response
                break;
            }
            // Add read line to the list of lines
            lines=lines+"\n"+line;
        }
        return lines;
    }

}
