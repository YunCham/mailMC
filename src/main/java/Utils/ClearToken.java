package utils;

import controller.*;
import model.*;
import model.DAO.*;

public class ClearToken {

    private String token;
    private RegularExpressionValidator validateToken;
    private String[] tokenDividido;
    public String remitente;

//    public ClearToken() {
//        this.token = "";
//    }

    public ClearToken(String token) {
        this.token = obtenerDesdeGuion(token);
        this.validateToken = new RegularExpressionValidator();
        this.remitente = limpiarDesdeGuion(token);
    }

    public boolean validateAndProcessToken() {

        System.out.printf(this.token);

        // Validación de cada parte del token
        if (!validateToken.validartokenTotal(this.token)) {
            System.out.println("Error: Controller Token no cumple con el formato requerido.");
            return false;
        }

        this.tokenDividido = this.token.split("_"); // Separando query por palabra

        if (tokenDividido.length < 5) {
            System.out.println("Formato de token incorrecto. Debe tener al menos 5 partes separadas por '_'.");
            return false;
        }

        String controllerType   =     tokenDividido[0];                 //Nombre especifico sobre la tabla que operara
        String operationType    =     tokenDividido[1];                 //Tipo de query (INSERT, UPDATE, SELECT and DELETE)
        String operationValue   =     tokenDividido[2].toLowerCase();   //Valores del query
        String user             =     tokenDividido[3].toLowerCase();   //User q realiza el query
        String password         =     tokenDividido[4];                 //Pass del user q realiza la query

        // Validación de cada parte del token
        if (!validateToken.validarCuatroLetrasMayusculas(controllerType)) {
            System.out.println("Error: Controller Type no cumple con el formato requerido.");
            return false;
        }

        if (!validateToken.validarMetodos(operationType)) {
            System.out.println("Error: Operation Type no cumple con el formato requerido.");
            return false;
        }

        if (!validateToken.validarCadenaEntreParentesis(operationValue)) {
            System.out.println("Error: Parametros no cumple con el formato requerido.");
            return false;
        }

        if (!validateToken.validarCorreoElectronico(user)) {
            System.out.println("Error: Correo no cumple con el formato requerido.");
            return false;
        }

//        Usuario usuario = new Usuario();
//        String param = user + ',' + password;
//        if (!usuario.verifyUser(param)) {
//            System.out.println("Correo o contraseña incorrectos.");
//            return false;
//        }

        processControllerOperation(controllerType, operationType, operationValue);
        return true;
    }

    private void processControllerOperation(String controllerType, String operationType, String operationValue) {
        switch (controllerType) {
            case "HELP":
                //new AyudaController().llamarAyuda();
                break;
            case "CLIE":
                UserDAO userDAO = new UserDAO();
                UserModel newUser = new UserModel();
                String[] values = ClearString.clear(operationValue).split(",");

                switch (operationType) {
                    case "INSERT":
                        newUser.setName(values[1].trim());
                        newUser.setEmail(values[2].trim());
                        newUser.setPassword(values[3].trim());
                        newUser.setPhone(values[4].trim());
                        newUser.setAddress(values[5].trim());
                        break;

                    case "UPDATE":
                        if (values.length >= 6) {
                            newUser.setId(Integer.parseInt(values[0].trim()));
                            newUser.setName(values[1].trim());
                            newUser.setEmail(values[2].trim());
                            newUser.setPassword(values[3].trim());
                            newUser.setPhone(values[4].trim());
                            newUser.setAddress(values[5].trim());
                        } else if (values.length == 2) {                //solucionar
                            newUser.setEmail(values[0].trim());
                            newUser.setPassword(values[1].trim());
                        }
                        break;

                    case "DELETE":
                        if (values.length == 1) {
                            if (values[0].contains("@")) {
                                newUser.setEmail(values[0].trim());  //solo por el email
                            } else {
                                try {
                                    newUser.setId(Integer.parseInt(values[0].trim()));
                                } catch (NumberFormatException e) {
                                    newUser.setName(values[0].trim());
                                }
                            }
                        }
                        break;

                    case "SELECT":
                        if (operationValue.equals("*")) {
                            newUser = null; // Seleccionar todos los registros
                        } else if (values.length == 1) {
                            if (values[0].contains("@")) {
                                newUser.setEmail(values[0].trim());
                            } else {
                                try {
                                    newUser.setId(Integer.parseInt(values[0].trim()));
                                } catch (NumberFormatException e) {
                                    newUser.setName(values[0].trim());
                                }
                            }
                        }
                        break;

                    default:
                        System.out.println("Operación no reconocida.");
                        return;
                }

                new UserController(userDAO).operationSet( this.remitente, operationType, newUser);
                break;
            case "USER":
//                new UsuarioController().operationSet(operationType, operationValue);
                break;
            case "PEDI":
                if ("INSERT".equals(operationType)) {
                    operationValue = tokenDividido[3] + "," + operationValue;
                }
//                new PedidoController().operationSet(operationType, operationValue);
                break;
            default:
                System.out.println("Consulta no válida");
        }
    }

    public static String limpiarDesdeGuion(String texto) {
        int indiceGuion = texto.indexOf('-');
        if (indiceGuion != -1) {
            return texto.substring(0, indiceGuion).trim();
        }
        return texto.trim();
    }

    public static String obtenerDesdeGuion(String texto) {
        int indiceGuion = texto.indexOf('-');
        if (indiceGuion != -1 && indiceGuion < texto.length() - 1) {
            return texto.substring(indiceGuion + 1).trim();
        }
        return "";
    }
}
