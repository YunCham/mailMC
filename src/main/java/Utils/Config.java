package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    private static Properties properties = new Properties();

    static {
        try (InputStream input = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Lo siento, no se encontro el archivo de configuracion.");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

// Credenciales para la Base de datos
    public static String getDbUrl() {
        return properties.getProperty("db.url");
    }

    public static String getDbnameDD() {
        return properties.getProperty("db.namedb");
    }

    public static String getDbUsername() {
        return properties.getProperty("db.username");
    }

    public static String getDbPassword() {
        return properties.getProperty("db.password");
    }

    // Credenciales para las Email
    public static String getMailSmtpHost() {
        return properties.getProperty("mail.smtp.host");
    }

    public static int getMailSmtpPort() {
        String portString = properties.getProperty("mail.smtp.port");
        return Integer.parseInt(portString);
    }

    public static int getMailPopPort() {
        String portString = properties.getProperty("mail.pop.port");
        return Integer.parseInt(portString);    }

    public static String getMailSmtpUser() {
        return properties.getProperty("mail.smtp.user");
    }

    public static String getMailSmtpUsername() {
        return properties.getProperty("mail.smtp.username");
    }

    public static String getMailSmtpPassword() {
        return properties.getProperty("mail.smtp.password");
    }
}
