package utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionValidator {

    // Expresión regular para validar un correo electrónico
    private static final String regexCorreoElectronico = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    // Expresión regular para validar que una cadena esté dentro de paréntesis
    private static final String regexCadenaEntreParentesis = "^\\((.*?)\\)$";

    // Expresión regular para validar que una cadena tenga exactamente 4 letras mayúsculas
    private static final String regexCuatroLetrasMayusculas = "^[A-Z]{4}$";

    // Expresión regular para validar los métodos INSERT, UPDATE, DELETE o SELECT
    private static final String regexMetodos = "^(INSERT|UPDATE|DELETE|SELECT)$";

    private  static  final  String tokenValidar = "^[A-Z]{4}_(INSERT|UPDATE|SELECT|DELETE)_\\(([^()]+)\\)_[\\w\\.-]+@[\\w\\.-]+\\.[a-zA-Z]{2,}_\\S+$";

    public boolean validartokenTotal( String token ){
        Pattern pattern = Pattern.compile(tokenValidar);
        Matcher matcher = pattern.matcher(token);
        return matcher.matches();
    }

    // Método para validar un correo electrónico
    public boolean validarCorreoElectronico(String correo) {
        Pattern pattern = Pattern.compile(regexCorreoElectronico);
        Matcher matcher = pattern.matcher(correo);
        return matcher.matches();
    }

    // Método para validar una cadena dentro de paréntesis
    public boolean validarCadenaEntreParentesis(String cadena) {
        Pattern pattern = Pattern.compile(regexCadenaEntreParentesis);
        Matcher matcher = pattern.matcher(cadena);
        return matcher.matches();
    }

    // Método para validar que una cadena tenga exactamente 4 letras mayúsculas
    public boolean validarCuatroLetrasMayusculas(String cadena) {
        Pattern pattern = Pattern.compile(regexCuatroLetrasMayusculas);
        Matcher matcher = pattern.matcher(cadena);
        return matcher.matches();
    }

    // Método para validar los métodos INSERT, UPDATE, DELETE o SELECT
    public boolean validarMetodos(String metodo) {
        Pattern pattern = Pattern.compile(regexMetodos);
        Matcher matcher = pattern.matcher(metodo);
        return matcher.matches();
    }
}
