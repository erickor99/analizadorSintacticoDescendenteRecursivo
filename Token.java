public class Token {

    private final TokenType tipo;
    private final String lexema;
    private final Object literal;
    private final int linea;

    public Token(TokenType tipo, String lexema, Object literal, int linea) {
        this.tipo = tipo;
        this.lexema = lexema;
        this.literal = literal;
        this.linea = linea;
    }

    public TokenType getTipo() {
        return tipo;
    }

    public String getLexema() {
        return lexema;
    }

    public Object getLiteral() {
        return literal;
    }

    public int getLinea() {
        return linea;
    }

    @Override
    public String toString() {
        return tipo + " " + lexema + " " + literal;
    }
}
