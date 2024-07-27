package jlox.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import jlox.scanner.Token;
import jlox.scanner.TokenType;

class ParserTest {
    @Test
    public void testParsingPrimaryExprNumber() {
        final List<Token> ts = Arrays.asList(
                TokenFactory.make(TokenType.NUMBER, "12")
        );
        final Parser p = new Parser(ts);
        final Expr e = p.parse();
        final Expr.Literal expectedExpr = new Expr.Literal(12.0);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprString() {
        final String str = "thebrownfoxjumpedoverthewhitefence";
        final List<Token> ts = Arrays.asList(
                TokenFactory.make(TokenType.STRING, str)
        );
        final Parser p = new Parser(ts);
        final Expr e = p.parse();
        final Expr.Literal expectedExpr = new Expr.Literal(str);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprBoolTrue() {
        final String bool = "true";
        final List<Token> ts = Arrays.asList(
                TokenFactory.make(TokenType.TRUE, bool)
        );
        final Parser p = new Parser(ts);
        final Expr e = p.parse();
        final Expr.Literal expectedExpr = new Expr.Literal(true);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprBoolFalse() {
        final String bool = "false";
        final List<Token> ts = Arrays.asList(
                TokenFactory.make(TokenType.FALSE, bool)
        );
        final Parser p = new Parser(ts);
        final Expr e = p.parse();
        final Expr.Literal expectedExpr = new Expr.Literal(false);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprNil() {
        final String nil = "nil";
        final List<Token> ts = Arrays.asList(
                TokenFactory.make(TokenType.NIL, nil)
        );
        final Parser p = new Parser(ts);
        final Expr e = p.parse();
        final Expr.Literal expectedExpr = new Expr.Literal(null);

        assertTrue(e instanceof Expr.Literal);
        assertEquals(expectedExpr.value, ((Expr.Literal)e).value);
    }

    @Test
    public void testParsingPrimaryExprGrouping() {
        final List<Token> ts = Arrays.asList(
                TokenFactory.make(TokenType.LEFT_PAREN),
                TokenFactory.make(TokenType.NUMBER, "12"),
                TokenFactory.make(TokenType.RIGHT_PAREN)
        );
        final Parser p = new Parser(ts);
        final Expr e = p.parse();
        assertTrue(e instanceof Expr.Grouping);
    }

    @Test
    public void testParsingPrimaryExprGroupingFail() {
        final List<Token> ts = Arrays.asList(
                TokenFactory.make(TokenType.LEFT_PAREN),
                TokenFactory.make(TokenType.NUMBER, "12")
        );
        final Parser p = new Parser(ts);
        assertNull(p.parse());
        assertTrue(p.hasError());
    }

    @Test
    public void testParsingCompoundExpr() {
        final List<Token> ts = Arrays.asList(
                TokenFactory.make(TokenType.MINUS),
                TokenFactory.make(TokenType.NUMBER, "12"),
                TokenFactory.make(TokenType.PLUS),
                TokenFactory.make(TokenType.NUMBER, "3"),
                TokenFactory.make(TokenType.GREATER),
                TokenFactory.make(TokenType.NUMBER, "4"),
                TokenFactory.make(TokenType.EQUAL_EQUAL, "=="),
                TokenFactory.make(TokenType.NUMBER, "1"),
                TokenFactory.make(TokenType.LESS),
                TokenFactory.make(TokenType.NUMBER, "2"),
                TokenFactory.make(TokenType.STAR),
                TokenFactory.make(TokenType.NUMBER, "3"),
                TokenFactory.make(TokenType.EOF, "")
        );
        final Parser p = new Parser(ts);

        final AstPrinter printer = new AstPrinter();
        final String s = printer.print(p.parse());

        // It is not a good practice to test one class with another, which is not 100% tested, but I am lazy to check this big expression manually.
        assertEquals("(== (> (+ (- 12.0) 3.0) 4.0) (< 1.0 (* 2.0 3.0)))", s);
    }
    

    @Test
    public void testGroupingExprWithMissingOpeningParen() {
        final List<Token> ts = Arrays.asList(
            TokenFactory.make(TokenType.NUMBER, "42"),
            TokenFactory.make(TokenType.RIGHT_PAREN)
        );
        final Parser p = new Parser(ts);

        assertNull(p.parse());
        assertTrue(p.hasError());
    }

    @Test
    public void testGroupingExprWithWrongParenOrder() {
        final List<Token> ts = Arrays.asList(
            TokenFactory.make(TokenType.RIGHT_PAREN),
            TokenFactory.make(TokenType.NUMBER, "3"),
            TokenFactory.make(TokenType.LEFT_PAREN)
        );
        final Parser p = new Parser(ts);

        assertNull(p.parse());
        assertTrue(p.hasError());
    }

    @Test
    public void testCorrectUnaryExpr() {
        final List<Token> ts = Arrays.asList(
            TokenFactory.make(TokenType.MINUS),
            TokenFactory.make(TokenType.NUMBER, "3")
        );
        final Parser p = new Parser(ts);

        final Expr result = p.parse();
        assertNotNull(result);
        assertTrue(result instanceof Expr.Unary);
        final Expr.Unary castedResult = (Expr.Unary)result;
        assertEquals(TokenType.MINUS, castedResult.operator.type);
        assertTrue(castedResult.right instanceof Expr.Literal);
        final Expr.Literal castedRightExpr = (Expr.Literal)castedResult.right;
        assertEquals(3.0, castedRightExpr.value);
    }

    @Test
    public void testIncorrectUnaryCommaExprWithString() {
        final List<Token> ts = Arrays.asList(
            TokenFactory.make(TokenType.COMMA),
            TokenFactory.make(TokenType.STRING, "dr. spok")
        );
        final Parser p = new Parser(ts);
        final Expr result = p.parse();

        assertFalse(result instanceof Expr.Unary);
    }

    @Test
    public void testCorrectUnaryPlusExprWithNumber() {
        final List<Token> ts = Arrays.asList(
            TokenFactory.make(TokenType.PLUS),
            TokenFactory.make(TokenType.NUMBER, "3")
        );
        final Parser p = new Parser(ts);
        final Expr result = p.parse();

        assertNotNull(result);
    }

    @Test
    public void testIncorrectComparisonExpr() {
        final List<Token> ts = Arrays.asList(
            TokenFactory.make(TokenType.NUMBER, "0"),
            TokenFactory.make(TokenType.LESS),
            TokenFactory.make(TokenType.GREATER),
            TokenFactory.make(TokenType.NUMBER, "1")  
        );
        final Parser p = new Parser(ts);
        final Expr result = p.parse();

        assertNull(result);
        assertTrue(p.hasError());
    }

    @Test
    public void testIncorrectEqualityExpr() {
        final List<Token> ts = Arrays.asList(
            TokenFactory.make(TokenType.NUMBER, "0"),
            TokenFactory.make(TokenType.BANG_EQUAL, "!="),
            TokenFactory.make(TokenType.EQUAL),
            TokenFactory.make(TokenType.NUMBER, "1")
        );
        final Parser p = new Parser(ts);
        final Expr result = p.parse();

        assertNull(result);
        assertTrue(p.hasError());
    }

    @Test
    public void testBinaryExprWithoutLeftSide() {
        final List<Token> ts = Arrays.asList(
            TokenFactory.make(TokenType.STAR),
            TokenFactory.make(TokenType.NUMBER, "4")
        );
        final Parser p = new Parser(ts);
        final Expr result = p.parse();

        assertNull(result);
        assertTrue(p.hasError());
    }
}
