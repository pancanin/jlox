package jlox.interpreter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import jlox.parser.TokenFactory;
import jlox.scanner.TokenType;
import jlox.parser.Expr;

/**
 * InterpreterTest
 */
class InterpreterTest {

    @Test
    public void testUnaryMinusWithNumber() {
        final Expr e = new Expr.Unary(TokenFactory.make(TokenType.MINUS), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals(-3.0, (Double)res);
    }

    @Test
    public void testUnaryNegateWithBool() {
        final Expr e = new Expr.Unary(TokenFactory.make(TokenType.BANG), new Expr.Literal(true));
        final Interpreter i = new Interpreter();
        
        final Object res = i.interpret(e);

        assertNotNull(res);
        assertFalse((boolean)res);
    }

    @Test
    public void testGroupingExpr() {
        final Expr e = new Expr.Grouping(new Expr.Literal("hi"));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals("hi", res.toString());
    }

    @Test
    public void testBinaryPlusExprWithNumbers() {
        final Expr e = new Expr.Binary(new Expr.Literal(2.0), TokenFactory.make(TokenType.PLUS), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals(5.0, (Double)res);
    }

    @Test
    public void testBinaryPlusExprWithStrings() {
        final Expr e = new Expr.Binary(new Expr.Literal("Hello, "), TokenFactory.make(TokenType.PLUS), new Expr.Literal("World!"));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals("Hello, World!", res.toString());
    }

    @Test
    public void testBinaryPlusExprWithStringAndNumber() {
        final Expr e = new Expr.Binary(new Expr.Literal("The ans is: "), TokenFactory.make(TokenType.PLUS), new Expr.Literal(42.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals("The ans is: 42.0", res.toString());
    }

    @Test
    public void testBinaryPlusExprWithNumberAndString() {
        final Expr e = new Expr.Binary(new Expr.Literal(42.0), TokenFactory.make(TokenType.PLUS), new Expr.Literal("The ans is: "));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNull(res);
    }

    @Test
    public void testBinaryMinusExprWithNumbers() {
        final Expr e = new Expr.Binary(new Expr.Literal(2.0), TokenFactory.make(TokenType.MINUS), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals(-1.0, (Double)res);
    }

    @Test
    public void testBinaryMinusExprWithString() {
        final Expr e = new Expr.Binary(new Expr.Literal("2.0"), TokenFactory.make(TokenType.MINUS), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNull(res);
    }

    @Test
    public void testBinaryDivisionWithNumbers() {
        final Expr e = new Expr.Binary(new Expr.Literal(15.0), TokenFactory.make(TokenType.SLASH), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals(5.0, (Double)res);
    }

    @Test
    public void testBinaryDivisionWithZero() {
        final Expr e = new Expr.Binary(new Expr.Literal(15.0), TokenFactory.make(TokenType.SLASH), new Expr.Literal(0.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertTrue(Double.isInfinite((Double)res));
    }

    @Test
    public void testBinaryMultiWithNumbers() {
        final Expr e = new Expr.Binary(new Expr.Literal(15.0), TokenFactory.make(TokenType.STAR), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals(45.0, (Double)res);
    }

    @Test
    public void testBinaryComparisonLess() {
        final Expr e = new Expr.Binary(new Expr.Literal(2.0), TokenFactory.make(TokenType.LESS), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertTrue((Boolean)res);
    }

    @Test
    public void testBinaryComparisonLessWithStrAndNumber() {
        final Expr e = new Expr.Binary(new Expr.Literal("lemons"), TokenFactory.make(TokenType.LESS), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNull(res);
    }

    @Test
    public void testBinaryComparisonLessEqual() {
        final Expr e = new Expr.Binary(new Expr.Literal(2.0), TokenFactory.make(TokenType.LESS_EQUAL, "<="), new Expr.Literal(2.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertTrue((Boolean)res);
    }

    @Test
    public void testBinaryComparisonGreater() {
        final Expr e = new Expr.Binary(new Expr.Literal(2.0), TokenFactory.make(TokenType.GREATER), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertFalse((Boolean)res);
    }

    @Test
    public void testBinaryComparisonGreaterEqual() {
        final Expr e = new Expr.Binary(new Expr.Literal(2.0), TokenFactory.make(TokenType.GREATER_EQUAL, ">="), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertFalse((Boolean)res);
    }

    @Test
    public void testBinaryEquality() {
        final Expr e = new Expr.Binary(new Expr.Literal("Hi, mom!"), TokenFactory.make(TokenType.EQUAL_EQUAL), new Expr.Literal("hi, mom?"));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertFalse((Boolean)res);
    }

    @Test
    public void testBinaryInequality() {
        final Expr e = new Expr.Binary(new Expr.Literal("hi"), TokenFactory.make(TokenType.BANG_EQUAL), new Expr.Literal("hi"));
        final Interpreter i = new Interpreter();

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertFalse((Boolean)res);
    }
}
