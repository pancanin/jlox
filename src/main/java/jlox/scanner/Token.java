package jlox.scanner;

public class Token {
    public final TokenType type;
    public final String lexeme;// The string representation of the keyword, identifier or other token. For example, 'for', 'var', 'abc'
    public final Object literal; // The value of the string or numeric literal.
    public final int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    public Token(TokenType type, String lexeme, int line) {
        this(type, lexeme, lexeme, line);
    }
}
