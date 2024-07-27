package jlox.errors;

import jlox.scanner.Token;

/**
 * Error during interpretation.
 */
public class RuntimeError extends RuntimeException {
    private final Token token;

    public RuntimeError(Token token, String msg) {
        super(msg);
        this.token = token;
    }
    
    public Token getToken() {
        return token;
    }
}
