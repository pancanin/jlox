package jlox.parser;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import jlox.errors.ParseError;
import jlox.errors.UnexpectedTokenError;
import jlox.errors.Error;
import jlox.scanner.Token;
import jlox.scanner.TokenType;

/**
 * Accepts a list of tokens and creates expression tree - AST.
 * @author Valeri Hristov (valericfbg@gmail.com)
 */
public class Parser {
    
    
    /**
     * Keep a reference to the list of tokens, so we can iterate them one by one, do look-aheads and look-behinds.
     */
    private final List<Token> tokens;

    /**
     * The index of the token we reached so far.
     */
    private int currentIdx;
    
    private Error<ParseError> error;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        currentIdx = 0;
        error = Error.None();
    }

    /**
     * The entrypoint of the parser.
     * @return The Expr tree.
     */
    public Expr parse() {
        try {
            final Expr e = expression();

            // If there are more tokens after we constructed the tree, there must be something wrong with the script.
            if (more()) {
                throw new UnexpectedTokenError(current());
            }
            error = Error.None();
            return e;
        } catch (ParseError e) {
            error = new Error<ParseError>(e);
            return null;
        }
    }

    public Error<ParseError> getError() {
        return error; // Can I modify it fron outside?
    }

    /**
     * Entrypoint of parsing our tokens into AST.
     * This method should call the highest-precedence expression.
     * The idea is to descend down to the lowest precedence expression, for which we can immediately know the value, thus stop the recursion and then
     * build the tree on the way back.
     * @return The root of the AST.
     */
    private Expr expression() {
        return equality();
    }

    /**
     * Tries to parse an equality expression, for example, true == false.
     * If it cannot match an equality expression it moves on to the next precedence expression - comparison.
     * @return The node in the tree for the parsed equality expression.
     */
    private Expr equality() {
        return parseBinaryExpr(this::comparison, TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL);
    }

    /**
     * A comparison is something like, myvar > 0
     */
    private Expr comparison() {
        return parseBinaryExpr(this::term, TokenType.GREATER, TokenType.GREATER_EQUAL, TokenType.LESS, TokenType.LESS_EQUAL);
    }

    /**
     * This is for expressions like, 1 + 1, 3 - 2
     */
    private Expr term() {
        return parseBinaryExpr(this::factor, TokenType.PLUS, TokenType.MINUS);
    }

    /**
     * This is for expressions like, 42 * 3, 11 / 5
     */
    private Expr factor() {
        return parseBinaryExpr(this::unary, TokenType.STAR, TokenType.SLASH);
    }

    /**
     * Example unary expression: !true, -2, -myNumber, +1
     */
    private Expr unary() {
        if (matchTokensAdvance(TokenType.BANG, TokenType.MINUS, TokenType.PLUS)) {
            Token op = previous();
            return new Expr.Unary(op, unary());
        }
        return grouping();
    }

    /**
     * These are expressions in the form: (a < 0) && false -> the round parenthesis are giving more precedence to an expression. 
     * @return A grouping expression or makes a recursive call to primary()
     */
    private Expr grouping() {
        if (matchTokensAdvance(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consumeToken(TokenType.RIGHT_PAREN, "Expected closing ')' at the end of grouping expression.");
            return new Expr.Grouping(expr);
        }
        return primary();
    }

    /**
     * Primary expression is an expression which value we can know immediately.
     * @return A literal expression.
     */
    private Expr primary() {
        if (matchTokensAdvance(TokenType.FALSE)) return new Expr.Literal(false);
        if (matchTokensAdvance(TokenType.TRUE)) return new Expr.Literal(true);
        if (matchTokensAdvance(TokenType.NIL)) return new Expr.Literal(null);

        if (matchTokensAdvance(TokenType.STRING, TokenType.NUMBER)) {
            return new Expr.Literal(previous().literal);
        }

        // We could not match anything, so we throw an error.
        throw new ParseError(current(), "Expected an expression");
    }

    /**
     * Abstracts repetitive code around binary expressions.
     * Binary expressions are left-associative - if multiple equality expressions, like 'true == false == true == false', then we will
     * build the first equality from the left-most expression and then the result of that will be the left operand of another equality expression with the right hand side.
     * So, '(((true == false) == true) == false)'.
     * @param next the next, lower precedence expression.
     * @param types tokens to match for the particular expression.
     * @return binary expression, could be nested.
     */
    private Expr parseBinaryExpr(Supplier<Expr> next, TokenType... types) {
        Expr left = next.get();

        while (matchTokensAdvance(types)) {
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
    private void consumeToken(TokenType type, String errorMsg) throws ParseError {
        if (more() && current().type == type) {
            advance();
            return;
        }

        throw new ParseError(previous(), errorMsg);
    }

    private boolean matchTokens(TokenType... types) {
        if (!more()) { return false; }
        return Arrays.stream(types).anyMatch(type -> tokens.get(currentIdx).type == type);
    }

    private boolean matchTokensAdvance(TokenType... types) {
        if (matchTokens(types)) {
            advance();
            return true;
        }
        return false;
    }

    private void advance() {
        currentIdx++;
    }

    private boolean more() {
        return currentIdx < tokens.size() && tokens.get(currentIdx).type != TokenType.EOF;
    }

    private Token current() {
        return tokens.get(currentIdx);
    }

    private Token previous() {
        return tokens.get(currentIdx - 1);
    }
}
