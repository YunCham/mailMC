package communication;

import utils.Config;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EmailSend implements Runnable {

//    private static final String PORT_SMTP = String.valueOf(Config.getMailPopPort());
//    private static final String HOST = Config.getMailSmtpHost();
//    private static final String USER = Config.getMailSmtpUser();
//    private static final String MAIL_PASSWORD = Config.getMailSmtpPassword();
//    private static final String MAIL = Config.getMailSmtpUsername();
//
//    private final Email email;
//
//    public EmailSend(Email emailP) {
//        this.email = emailP;
//    }
//
//    @Override
//    public void run() {
//        Properties properties = new Properties();
//        properties.setProperty("mail.smtp.host", HOST);
//        properties.setProperty("mail.smtp.port", PORT_SMTP);
//        properties.setProperty("mail.smtp.auth", "true");
//        properties.setProperty("mail.smtp.starttls.enable", "true");
//        properties.setProperty("mail.smtp.ssl.trust", HOST); // Se asegura de confiar en el host
//
//        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
//            @Override
//            protected PasswordAuthentication getPasswordAuthentication() {
//                return new PasswordAuthentication(USER, MAIL_PASSWORD);
//            }
//        });
//
//        try {
//            MimeMessage message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(MAIL));
//            message.setRecipient(Message.RecipientType.TO, new InternetAddress(email.getTo()));
//            message.setSubject(email.getSubject());
//
//            Multipart multipart = new MimeMultipart();
//            MimeBodyPart htmlPart = new MimeBodyPart();
//            htmlPart.setContent(email.getMessage(), "text/html; charset=utf-8");
//            multipart.addBodyPart(htmlPart);
//
//            message.setContent(multipart);
//
//            // Enviar mensaje
//            Transport.send(message);
//            System.out.println("Email enviado exitosamente");
//
//        } catch (MessagingException ex) {
//            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, "Error al enviar el email", ex);
//        }
//    }
//


    private final static String PORT_SMTP = String.valueOf(Config.getMailPopPort());;
//    private final static String PROTOCOL = "smtp";
    private final static String HOST = Config.getMailSmtpHost();
    private final static String USER = Config.getMailSmtpUser();
//    private final static String PASSWORD = "grup006grup006";
    private final static String MAIL = Config.getMailSmtpUsername();
    private final static String MAIL_PASSWORD = Config.getMailSmtpPassword();

    private Email email;

    public EmailSend(Email emailP) {
        this.email = emailP;
        //this.email.setFrom(MAIL);
    }

    @Override
    public void run() {
        Properties properties = new Properties();
        //properties.put("mail.transport.protocol", PROTOCOL);
        properties.setProperty("mail.smtp.host", HOST);
        properties.setProperty("mail.smtp.port", PORT_SMTP);
        properties.setProperty("mail.smtp.tls.enable", "true");     //cuando user tecnoweb
        properties.setProperty("mail.smtp.ssl.enable", "*");        // cuando usen Gmail
        properties.setProperty("mail.smtp.auth", "false");          // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody

        Session session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(USER, MAIL_PASSWORD);
            }
        });

        try {
            MimeMessage message;
            message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAIL));
            InternetAddress[] toAddresses = { new InternetAddress(email.getTo())};

            message.setRecipients(MimeMessage.RecipientType.TO, toAddresses);
            message.setSubject(email.getSubject());

            Multipart multipart = new MimeMultipart("alternative");
            MimeBodyPart htmlPart = new MimeBodyPart();

            htmlPart.setContent(email.getMessage(), "text/html; charset=utf-8");
            multipart.addBodyPart(htmlPart);
            message.setContent(multipart);
            message.saveChanges();

            Transport.send(message);
        } catch (NoSuchProviderException | AddressException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MessagingException ex) {
            Logger.getLogger(EmailSend.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println(ex.toString());
        }
    }


}