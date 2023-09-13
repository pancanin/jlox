package main.scanner;

public class Token {
    final TokenType type;
    final String lexeme;// The string representation of the keyword, identifier or other token. For example, 'for', 'var', 'abc'
    final Object literal; // The value of the string or numeric literal.
    final int line;

    public Token(TokenType type, String lexeme, Object literal, int line) {
        this.type = type;
        this.lexeme = lexeme;
        this.literal = literal;
        this.line = line;
    }

    @Override
    public String toString() {
        return "Token{" +
                "type=" + type +
                ", lexeme='" + lexeme + '\'' +
                ", literal=" + literal +
                ", line=" + line +
                '}';
    }
}
