package interpreter.parser;

import interpreter.scanner.Token;

public class ParseError extends RuntimeException {
    private Token token;
    private String msg;

    public ParseError(Token token, String msg) {
        this.token = token;
        this.msg = msg;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
