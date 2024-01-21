package main.parser;

import main.scanner.Token;
import main.scanner.TokenType;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {
    @Test
    public void testParsingPrimaryExprNumber() {
        List<Token> ts = Arrays.asList(
                new Token(TokenType.NUMBER, "12", 12, 1)
        );
        Parser p = new Parser(ts);
        Expr e = p.parse();
        Expr.Literal expectedExpr = new Expr.Literal(12);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprString() {
        String str = "thebrownfoxjumpedoverthewhitefence";
        List<Token> ts = Arrays.asList(
                new Token(TokenType.STRING, str, str, 1)
        );
        Parser p = new Parser(ts);
        Expr e = p.parse();
        Expr.Literal expectedExpr = new Expr.Literal(str);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprBoolTrue() {
        String bool = "true";
        List<Token> ts = Arrays.asList(
                new Token(TokenType.TRUE, bool, bool, 1)
        );
        Parser p = new Parser(ts);
        Expr e = p.parse();
        Expr.Literal expectedExpr = new Expr.Literal(true);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprBoolFalse() {
        String bool = "false";
        List<Token> ts = Arrays.asList(
                new Token(TokenType.FALSE, bool, bool, 1)
        );
        Parser p = new Parser(ts);
        Expr e = p.parse();
        Expr.Literal expectedExpr = new Expr.Literal(false);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprNil() {
        String nil = "nil";
        List<Token> ts = Arrays.asList(
                new Token(TokenType.NIL, nil, nil, 1)
        );
        Parser p = new Parser(ts);
        Expr e = p.parse();
        Expr.Literal expectedExpr = new Expr.Literal(null);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprGrouping() {
        List<Token> ts = Arrays.asList(
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(TokenType.NUMBER, "12", 12, 1),
                new Token(TokenType.RIGHT_PAREN, ")", ")", 1)
        );
        Parser p = new Parser(ts);
        Expr e = p.parse();
        assertTrue(e instanceof Expr.Grouping);
    }

    @Test
    public void testParsingPrimaryExprGroupingFail() {
        List<Token> ts = Arrays.asList(
                new Token(TokenType.LEFT_PAREN, "(", "(", 1),
                new Token(TokenType.NUMBER, "12", 12, 1)
        );
        Parser p = new Parser(ts);
        assertNull(p.parse());
    }

    @Test
    public void testParsingCompoundExpr() {
        List<Token> ts = Arrays.asList(
                new Token(TokenType.MINUS, "-", "-", 1),
                new Token(TokenType.NUMBER, "12", 12, 1),
                new Token(TokenType.PLUS, "+", "+", 1),
                new Token(TokenType.NUMBER, "3", 3, 1),
                new Token(TokenType.GREATER, ">", ">", 1),
                new Token(TokenType.NUMBER, "4", 4, 1),
                new Token(TokenType.EQUAL_EQUAL, "==", "==", 1),
                new Token(TokenType.NUMBER, "1", 1, 1),
                new Token(TokenType.LESS, "<", "<", 1),
                new Token(TokenType.NUMBER, "2", "2", 1),
                new Token(TokenType.STAR, "*", "*", 1),
                new Token(TokenType.NUMBER, "3", "3", 1),
                new Token(TokenType.EOF, "", "", 1)
        );
        Parser p = new Parser(ts);

        AstPrinter printer = new AstPrinter();
        String s = printer.print(p.parse());

        assertEquals("(== (> (+ (- 12) 3) 4) (< 1 (* 2 3)))", s);
    }
}