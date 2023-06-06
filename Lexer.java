import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lexer {

    private final String codigoFuente;
    private final List<Token> tokens;
    private int inicio;
    private int actual;
    private int linea;

    private static final Map<String, TokenType> PALABRAS_RESERVADAS;

    static {
        PALABRAS_RESERVADAS = new HashMap<>();
        PALABRAS_RESERVADAS.put("class", TokenType.CLASS);
        PALABRAS_RESERVADAS.put("fun", TokenType.FUN);
        PALABRAS_RESERVADAS.put("var", TokenType.VAR);
        PALABRAS_RESERVADAS.put("for", TokenType.FOR);
        PALABRAS_RESERVADAS.put("if", TokenType.IF);
        PALABRAS_RESERVADAS.put("else", TokenType.ELSE);
        PALABRAS_RESERVADAS.put("print", TokenType.PRINT);
        PALABRAS_RESERVADAS.put("return", TokenType.RETURN);
        PALABRAS_RESERVADAS.put("while", TokenType.WHILE);
        PALABRAS_RESERVADAS.put("true", TokenType.TRUE);
        PALABRAS_RESERVADAS.put("false", TokenType.FALSE);
        PALABRAS_RESERVADAS.put("null", TokenType.NULL);
        PALABRAS_RESERVADAS.put("this", TokenType.THIS);
        PALABRAS_RESERVADAS.put("super", TokenType.SUPER);
    }

    public Lexer(String codigoFuente) {
        this.codigoFuente = codigoFuente;
        this.tokens = new ArrayList<>();
        this.inicio = 0;
        this.actual = 0;
        this.linea = 1;
    }

    public List<Token> analizarTokens() {
        while (!finCodigo()) {
            inicio = actual;
            analizarToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, linea));
        return tokens;
    }

    private void analizarToken() {
        char c = avanzar();

        switch (c) {
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
            case ';':
                agregarToken(TokenType.PUNTO_COMA);
                break;
            case '+':
                agregarToken(TokenType.MAS);
                break;
            case '-':
                agregarToken(TokenType.MENOS);
                break;
            case '*':
                agregarToken(TokenType.MULTIPLICACION);
                break;
            case '/':
                agregarToken(TokenType.DIVISION);
                break;
            case '!':
                agregarToken(igual('=') ? TokenType.DIFERENTE : TokenType.NOT);
                break;
            case '=':
                agregarToken(igual('=') ? TokenType.IGUAL_IGUAL : TokenType.IGUAL);
                break;
            case '<':
                agregarToken(igual('=') ? TokenType.MENOR_IGUAL : TokenType.MENOR);
                break;
            case '>':
                agregarToken(igual('=') ? TokenType.MAYOR_IGUAL : TokenType.MAYOR);
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                linea++;
                break;
            case '"':
                analizarCadena();
                break;
            default:
                if (esDigito(c)) {
                    analizarNumero();
                } else if (esLetra(c)) {
                    analizarIdentificador();
                } else {
                    // Carácter no reconocido
                    tokens.add(new ErrorToken(linea, "Carácter no reconocido: " + c));
                }
                break;
        }
    }

    private void agregarToken(TokenType tipo) {
        agregarToken(tipo, null);
    }

    private void agregarToken(TokenType tipo, Object literal) {
        String lexema = codigoFuente.substring(inicio, actual);
        tokens.add(new Token(tipo, lexema, literal, linea));
    }

    private boolean finCodigo() {
        return actual >= codigoFuente.length();
    }

    private char avanzar() {
        return codigoFuente.charAt(actual++);
    }

    private boolean igual(char c) {
        if (finCodigo())
            return false;
        if (codigoFuente.charAt(actual) != c)
            return false;
        actual++;
        return true;
    }

    private boolean esDigito(char c) {
        return c >= '0' && c <= '9';
    }

    private boolean esLetra(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    private void analizarCadena() {
        while (!finCodigo() && codigoFuente.charAt(actual) != '"') {
            if (codigoFuente.charAt(actual) == '\n')
                linea++;
            actual++;
        }

        if (finCodigo()) {
            tokens.addErrorToken(linea, "Cadena no cerrada.");
            return;
        }

        actual++;
        String lexema = codigoFuente.substring(inicio + 1, actual - 1);
        tokens.add(new Token(TokenType.CADENA, lexema, lexema, linea));
    }

    private void analizarNumero() {
        while (!finCodigo() && esDigito(codigoFuente.charAt(actual))) {
            actual++;
        }

        if (!finCodigo() && codigoFuente.charAt(actual) == '.' && esDigito(siguienteChar())) {
            actual++;

            while (!finCodigo() && esDigito(codigoFuente.charAt(actual))) {
                actual++;
            }
        }

        String lexema = codigoFuente.substring(inicio, actual);
        double valor = Double.parseDouble(lexema);
        tokens.add(new Token(TokenType.NUMERO, lexema, valor, linea));
    }

    private char siguienteChar() {
        if (actual + 1 >= codigoFuente.length())
            return '\0';
        return codigoFuente.charAt(actual + 1);
    }

    private void analizarIdentificador() {
        while (!finCodigo() && (esLetra(codigoFuente.charAt(actual)) || esDigito(codigoFuente.charAt(actual)))) {
            actual++;
        }

        String lexema = codigoFuente.substring(inicio, actual);
        TokenType tipo = PALABRAS_RESERVADAS.getOrDefault(lexema, TokenType.IDENTIFICADOR);
        tokens.add(new Token(tipo, lexema, null, linea));
    }
}
