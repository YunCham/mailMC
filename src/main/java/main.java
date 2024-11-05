import communication.Email;
import communication.EmailSend;
import controller.UserController;
import model.DAO.UserDAO;
import model.UserModel;

public class  main {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        UserController userController = new UserController(userDAO);

        // Crear un nuevo usuario
        UserModel newUser = new UserModel();
//        newUser.setId(1);
//        newUser.setName("Jhonn Perez");
//        newUser.setEmail("admin@gmail.com");
//        newUser.setPassword("holamundo");
//        newUser.setPhone("7654323");
//        newUser.setAddress("Calle villa 123");

        // Intentar insertar el nuevo usuario
//        userController.operationSet("UPDATE", newUser);
//        String resultMessage = userController.insertUser(newUser);
//        System.out.println(resultMessage);


//        login
//        userController.userLogin(newUser);


        String destinatario = "yuncham2.0@gmail.com";
        String asunto = "Asunto del correo";
        String mensaje = "<h1>Hola, este es un correo de prueba</h1>";

        Email email = new Email(destinatario, asunto, mensaje);

        // Crear y ejecutar el hilo para enviar el correo
        EmailSend emailSender = new EmailSend(email);
        Thread thread = new Thread(emailSender);
        thread.start();
    }
}
