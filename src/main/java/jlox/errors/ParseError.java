package jlox.errors;

import jlox.scanner.Token;

public class ParseError extends RuntimeException {
    private final Token token;

    public ParseError(Token token, String msg) {
        super(msg);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
