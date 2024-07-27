package interpreter.parser;

import interpreter.scanner.Token;

public class ParseError extends RuntimeException {
    private final Token token;
    private final String msg;

    public ParseError(Token token, String msg) {
        this.token = token;
        this.msg = msg;
    }

    public Token getToken() {
        return token;
    }

    public String getMsg() {
        return msg;
    }
}
