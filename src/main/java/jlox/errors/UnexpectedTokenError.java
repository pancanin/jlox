package jlox.errors;

import jlox.scanner.Token;

/**
 * This is a type of runtime error and usually happens when the syntax of the code is incorrect.
 */
public class UnexpectedTokenError extends ParseError {
    public UnexpectedTokenError(Token token, String msg) {
        super(token, String.format("Unexpected token.%s", msg.isEmpty() ? "" : (" " + msg)));
    }

    public UnexpectedTokenError(Token token) {
        this(token, "");
    }
}
