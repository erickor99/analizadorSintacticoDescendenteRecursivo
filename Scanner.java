import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String, TokenType> palabrasClave = new HashMap<>();

    static {
        palabrasClave.put("class", TokenType.CLASS);
        palabrasClave.put("fun", TokenType.FUN);
        palabrasClave.put("var", TokenType.VAR);
        palabrasClave.put("for", TokenType.FOR);
        palabrasClave.put("if", TokenType.IF);
        palabrasClave.put("else", TokenType.ELSE);
        palabrasClave.put("print", TokenType.PRINT);
        palabrasClave.put("return", TokenType.RETURN);
        palabrasClave.put("while", TokenType.WHILE);
        palabrasClave.put("true", TokenType.TRUE);
        palabrasClave.put("false", TokenType.FALSE);
        palabrasClave.put("null", TokenType.NULL);
        palabrasClave.put("this", TokenType.THIS);
        palabrasClave.put("super", TokenType.SUPER);
    }

    private int inicio = 0;
    private int actual = 0;
    private int linea = 1;

    public Scanner(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!seHaTerminado()) {
            inicio = actual;
            escanearToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, linea));
        return tokens;
    }

    private void escanearToken() {
        char c = avanzar();
        switch (c) {
            // Casos para operadores y puntuación
            case '(':
                agregarToken(TokenType.PARENTESIS_IZQ);
                break;
            case ')':
                agregarToken(TokenType.PARENTESIS_DER);
                break;
            case '{':
                agregarToken(TokenType.LLAVE_IZQ);
                break;
            case '}':
                agregarToken(TokenType.LLAVE_DER);
                break;
            case ',':
                agregarToken(TokenType.COMA);
                break;
            case '.':
                agregarToken(TokenType.PUNTO);
                break;
            case '-':
                agregarToken(TokenType.MENOR);
                break;
            // Otros casos...
            default:
                Principal.error(linea, "Carácter inesperado.");
                break;
        }
    }

    private boolean seHaTerminado() {
        return actual >= source.length();
    }

    private char avanzar() {
        actual++;
        return source.charAt(actual - 1);
    }

    private void agregarToken(TokenType tipo) {
        agregarToken(tipo, null);
    }

    private void agregarToken(TokenType tipo, Object literal) {
        String lexema = source.substring(inicio, actual);
        tokens.add(new Token(tipo, lexema, literal, linea));
    }

}
