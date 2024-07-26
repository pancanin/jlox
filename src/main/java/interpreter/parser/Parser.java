package interpreter.parser;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import interpreter.scanner.Token;
import interpreter.scanner.TokenType;

public class Parser {

    private final List<Token> tokens;
    private int current = 0;
    private boolean hasError = false;
    private ParseError error;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public Expr parse() {
        try {
            return expression();
        } catch (ParseError e) {
            hasError = true;
            error = e;
            return null;
        }
    }

    public boolean hasError() {
        return hasError;
    }

    public ParseError getError() {
        return error;
    }

    // expression     → equality ;
    private Expr expression() {
        return equality();
    }

    // equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    private Expr equality() {
        return parseBinaryExpr(this::comparison, TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL);
    }

    // comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private Expr comparison() {
        return parseBinaryExpr(this::term, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL);
    }

    // term           → factor ( ( "-" | "+" ) factor )* ;
    private Expr term() {
        return parseBinaryExpr(this::factor, TokenType.PLUS, TokenType.MINUS);
    }

    private Expr factor() {
        return parseBinaryExpr(this::unary, TokenType.STAR, TokenType.SLASH);
    }

    // unary          → ( "!" | "-" ) unary | primary ;
    private Expr unary() {
        if (matchAdv(TokenType.BANG, TokenType.MINUS)) {
            Token op = previous();
            return new Expr.Unary(op, unary());
        }
        return primary();
    }

    private Expr primary() {
        if (matchAdv(TokenType.FALSE)) return new Expr.Literal(false);
        if (matchAdv(TokenType.TRUE)) return new Expr.Literal(true);
        if (matchAdv(TokenType.NIL)) return new Expr.Literal(null);

        if (matchAdv(TokenType.STRING, TokenType.NUMBER)) {
            return new Expr.Literal(previous().literal);
        }

        if (matchAdv(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expected closing )");
            return new Expr.Grouping(expr);
        }

        throw new ParseError(current(), "Expected an expression");
    }

    /**
     * Abstracts repetitive code around binary expressions
     * @param next the next, higher precedence expression
     * @param types tokens to match for the particular expression.
     * @return binary expression, could be nested
     */
    private Expr parseBinaryExpr(Supplier<Expr> next, TokenType... types) {
        Expr left = next.get();

        while (matchAdv(types)) {
            Token op = previous();
            Expr right = next.get();
            left = new Expr.Binary(left, op, right);
        }

        return left;
    }

    /**
     * Check if a token exists in the source code. We do this check when we expect a token, for example, closing brace,
     * after an opening one.
     * @param type The token type that we expect in the source code.
     * @param errorMsg When the token is not there, provide an appropriate error message to put in the Exception object.
     * @throws ParseError thrown if the expected token is not in the expected place in the source code.
     */
    private void consume(TokenType type, String errorMsg) throws ParseError {
        if (more() && current().type == type) {
            advance();
            return;
        }

        throw new ParseError(previous(), errorMsg);
    }

    private boolean match(TokenType... types) {
        if (!more()) { return false; }
        return Arrays.stream(types).anyMatch(type -> tokens.get(current).type == type);
    }

    private boolean matchAdv(TokenType... types) {
        if (match(types)) {
            advance();
            return true;
        }
        return false;
    }

    private void advance() {
        current++;
    }

    private boolean more() {
        return current < tokens.size() && tokens.get(current).type != TokenType.EOF;
    }

    private Token current() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }
}
