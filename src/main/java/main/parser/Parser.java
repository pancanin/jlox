package main.parser;

import main.scanner.Token;
import main.scanner.TokenType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // expression     → equality ;
    private Expr expression() {
        return equality();
    }

    // equality       → comparison ( ( "!=" | "==" ) comparison )* ;
    private Expr equality() {
        return binaryExprFactory(this::comparison, TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL);
    }

    // comparison     → term ( ( ">" | ">=" | "<" | "<=" ) term )* ;
    private Expr comparison() {
        return binaryExprFactory(this::term, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL);
    }

    // term           → factor ( ( "-" | "+" ) factor )* ;
    private Expr term() {
        return binaryExprFactory(this::factor, TokenType.PLUS, TokenType.MINUS);
    }

    private Expr factor() {
        return binaryExprFactory(this::unary, TokenType.STAR, TokenType.SLASH);
    }

    private Expr unary() {
        // TODO: Not implemented
    }

    /**
     * Abstracts repetitive code around binary expressions
     * @param next the next higher precedence expression
     * @param types
     * @return
     */
    private Expr binaryExprFactory(Supplier<Expr> next, TokenType... types) {
        Expr left = next.get();

        while (match(types)) {
            Token op = current();
            advance();
            Expr right = next.get();
            left = new Expr.Binary(left, op, right);
        }

        return left;
    }

    private boolean match(TokenType... types) {
        if (!more()) { return false; }
        return Arrays.stream(types).anyMatch(type -> tokens.get(current).type == type);
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
}
