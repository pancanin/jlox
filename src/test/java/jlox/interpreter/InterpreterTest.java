package jlox.interpreter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import jlox.parser.TokenFactory;
import jlox.scanner.TokenType;
import jlox.errors.ErrorReporter;
import jlox.parser.Expr;

/**
 * InterpreterTest
 */
class InterpreterTest {

    static ErrorReporter errorReporter;

    static {
        errorReporter = new ErrorReporter();
    }

    @Test
    public void testUnaryMinusWithNumber() {
        final Expr e = new Expr.Unary(TokenFactory.make(TokenType.MINUS), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter(errorReporter);

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals(-3.0, (Double)res);
    }

    @Test
    public void testUnaryNegateWithBool() {
        final Expr e = new Expr.Unary(TokenFactory.make(TokenType.BANG), new Expr.Literal(true));
        final Interpreter i = new Interpreter(errorReporter);
        
        final Object res = i.interpret(e);

        assertNotNull(res);
        assertFalse((boolean)res);
    }
}
