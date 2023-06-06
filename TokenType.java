public enum TokenType {
    // Palabras clave
    CLASS,
    VAR,
    FUN,
    FOR,
    IF,
    ELSE,
    PRINT,
    RETURN,
    WHILE,
    TRUE,
    FALSE,
    NULL,
    THIS,
    SUPER,

    // Operadores y puntuación
    IGUAL,
    IGUAL_IGUAL,
    DIFERENTE,
    MAYOR,
    MAYOR_IGUAL,
    MENOR,
    MENOR_IGUAL,
    SUMA,
    RESTA,
    MULTIPLICACION,
    DIVISION,
    NOT,
    AND,
    OR,
    PARENTESIS_IZQ,
    PARENTESIS_DER,
    LLAVE_IZQ,
    LLAVE_DER,
    PUNTO_COMA,
    COMA,
    PUNTO,
    MAS,
    MENOS,

    // Otros
    IDENTIFICADOR,
    NUMERO,
    CADENA,

    // Fin de archivo
    EOF
}
