import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int indiceActual;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.indiceActual = 0;
    }

    public void analizar() {
        while (!haTerminado()) {
            declaracion();
        }
    }

    private void declaracion() {
        if (empiezaCon(TokenType.CLASS)) {
            claseDeclaracion();
        } else if (empiezaCon(TokenType.FUN)) {
            funcionDeclaracion();
        } else if (empiezaCon(TokenType.VAR)) {
            variableDeclaracion();
        } else {
            sentencia();
        }
    }

    private void claseDeclaracion() {
        consumir(TokenType.CLASS, "Se esperaba la palabra clave 'class'.");
        Token nombreClase = consumir(TokenType.IDENTIFICADOR, "Se esperaba el nombre de la clase.");

        if (empiezaCon(TokenType.MENOR)) {
            consumir(TokenType.MENOR, "Se esperaba el símbolo '<'.");
            Token nombrePadre = consumir(TokenType.IDENTIFICADOR, "Se esperaba el nombre de la clase padre.");
            consumir(TokenType.MAYOR, "Se esperaba el símbolo '>'.");
        }

        consumir(TokenType.LLAVE_IZQ, "Se esperaba el símbolo '{'.");

        while (!comprobar(TokenType.LLAVE_DER) && !haTerminado()) {
            funcionDeclaracion();
        }

        consumir(TokenType.LLAVE_DER, "Se esperaba el símbolo '}'.");
    }

    private void funcionDeclaracion() {
        consumir(TokenType.FUN, "Se esperaba la palabra clave 'fun'.");
        Token nombreFuncion = consumir(TokenType.IDENTIFICADOR, "Se esperaba el nombre de la función.");
        consumir(TokenType.PARENTESIS_IZQ, "Se esperaba el símbolo '('.");
        parametrosOpcionales();
        consumir(TokenType.PARENTESIS_DER, "Se esperaba el símbolo ')'.");
        bloque();
    }

    private void parametrosOpcionales() {
        if (!comprobar(TokenType.PARENTESIS_DER)) {
            do {
                consumir(TokenType.IDENTIFICADOR, "Se esperaba un nombre de parámetro.");
            } while (empiezaCon(TokenType.COMA));
        }
    }

    private void variableDeclaracion() {
        consumir(TokenType.VAR, "Se esperaba la palabra clave 'var'.");
        consumir(TokenType.IDENTIFICADOR, "Se esperaba el nombre de la variable.");

        if (empiezaCon(TokenType.IGUAL)) {
            consumir(TokenType.IGUAL, "Se esperaba el símbolo '='.");
            expresion();
        }

        consumir(TokenType.PUNTO_COMA, "Se esperaba el símbolo ';'.");
    }

    private void sentencia() {
        if (empiezaCon(TokenType.PARENTESIS_IZQ)) {
            expresionSentencia();
        } else if (empiezaCon(TokenType.PRINT)) {
            imprimirSentencia();
        } else if (empiezaCon(TokenType.RETURN)) {
            retornoSentencia();
        } else if (empiezaCon(TokenType.LLAVE_IZQ)) {
            bloque();
        } else {
            // Otro tipo de sentencia
        }
    }

    private void expresionSentencia() {
        expresion();
        consumir(TokenType.PUNTO_COMA, "Se esperaba el símbolo ';'.");
    }

    private void imprimirSentencia() {
        consumir(TokenType.PRINT, "Se esperaba la palabra clave 'print'.");
        expresion();
        consumir(TokenType.PUNTO_COMA, "Se esperaba el símbolo ';'.");
    }

    private void retornoSentencia() {
        consumir(TokenType.RETURN, "Se esperaba la palabra clave 'return'.");
        if (!comprobar(TokenType.PUNTO_COMA)) {
            expresion();
        }
        consumir(TokenType.PUNTO_COMA, "Se esperaba el símbolo ';'.");
    }

    private void bloque() {
        consumir(TokenType.LLAVE_IZQ, "Se esperaba el símbolo '{'.");

        while (!comprobar(TokenType.LLAVE_DER) && !haTerminado()) {
            declaracion();
        }

        consumir(TokenType.LLAVE_DER, "Se esperaba el símbolo '}'.");
    }

    private void expresion() {
        asignacion();
    }

    private void asignacion() {
        logicaOr();

        if (empiezaCon(TokenType.IGUAL)) {
            consumir(TokenType.IGUAL, "Se esperaba el símbolo '='.");
            expresion();
        }
    }

    private void logicaOr() {
        logicaAnd();

        while (empiezaCon(TokenType.OR)) {
            consumir(TokenType.OR, "Se esperaba el operador lógico 'or'.");
            logicaAnd();
        }
    }

    private void logicaAnd() {
        igualdad();

        while (empiezaCon(TokenType.AND)) {
            consumir(TokenType.AND, "Se esperaba el operador lógico 'and'.");
            igualdad();
        }
    }

    private void igualdad() {
        comparacion();

        while (empiezaCon(TokenType.IGUAL_IGUAL) || empiezaCon(TokenType.DIFERENTE)) {
            Token operador = siguiente();
            comparacion();
        }
    }

    private void comparacion() {
        termino();

        while (empiezaCon(TokenType.MAYOR) || empiezaCon(TokenType.MAYOR_IGUAL) ||
                empiezaCon(TokenType.MENOR) || empiezaCon(TokenType.MENOR_IGUAL)) {
            Token operador = siguiente();
            termino();
        }
    }

    private void termino() {
        factor();

        while (empiezaCon(TokenType.SUMA) || empiezaCon(TokenType.RESTA)) {
            Token operador = siguiente();
            factor();
        }
    }

    private void factor() {
        unario();

        while (empiezaCon(TokenType.MULTIPLICACION) || empiezaCon(TokenType.DIVISION)) {
            Token operador = siguiente();
            unario();
        }
    }

    private void unario() {
        if (empiezaCon(TokenType.NOT) || empiezaCon(TokenType.RESTA)) {
            Token operador = siguiente();
            unario();
        } else {
            llamada();
        }
    }

    private void llamada() {
        primario();

        while (empiezaCon(TokenType.PARENTESIS_IZQ) || empiezaCon(TokenType.PUNTO)) {
            if (empiezaCon(TokenType.PARENTESIS_IZQ)) {
                argumentosOpcionales();
                consumir(TokenType.PARENTESIS_DER, "Se esperaba el símbolo ')'.");
            } else if (empiezaCon(TokenType.PUNTO)) {
                consumir(TokenType.PUNTO, "Se esperaba el símbolo '.'.");
                consumir(TokenType.IDENTIFICADOR, "Se esperaba el nombre del método o atributo.");
            }
        }
    }

    private void argumentosOpcionales() {
        if (!comprobar(TokenType.PARENTESIS_DER)) {
            do {
                expresion();
            } while (empiezaCon(TokenType.COMA));
        }
    }

    /* Métodos auxiliares */

    private boolean empiezaCon(TokenType tipo) {
        if (haTerminado()) {
            return false;
        }

        return tokens.get(indiceActual).getTipo() == tipo;
    }

    private boolean comprobar(TokenType tipo) {
        if (haTerminado()) {
            return false;
        }

        return tokens.get(indiceActual).getTipo() == tipo;
    }

    private Token consumir(TokenType tipo, String mensajeError) {
        if (comprobar(tipo)) {
            return siguiente();
        }

        throw new ErrorSintactico(tokens.get(indiceActual), mensajeError);
    }

    private Token siguiente() {
        if (!haTerminado()) {
            indiceActual++;
        }

        return tokens.get(indiceActual - 1);
    }

    private boolean haTerminado() {
        return indiceActual >= tokens.size();
    }
}
