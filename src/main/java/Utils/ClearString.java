package utils;

public class ClearString {

    /**
     * Elimina los caracteres especificados de una cadena de texto.
     *
     * @param cadena La cadena de texto que se va a limpiar.
     * @return La cadena de texto sin los caracteres especificados.
     */
    public static String clear(String cadena){
        return cadena
                .replace("(","")
                .replace(")","");
    }
}
