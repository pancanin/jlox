package jlox.interpreter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import jlox.parser.TokenFactory;
import jlox.scanner.TokenType;
import jlox.errors.ErrorLogger;
import jlox.parser.Expr;

/**
 * InterpreterTest
 */
class InterpreterTest {

    static ErrorLogger errorLogger;

    static {
        errorLogger = new ErrorLogger();
    }

    @Test
    public void testUnaryMinusWithNumber() {
        final Expr e = new Expr.Unary(TokenFactory.make(TokenType.MINUS), new Expr.Literal(3.0));
        final Interpreter i = new Interpreter(errorLogger);

        final Object res = i.interpret(e);

        assertNotNull(res);
        assertEquals(-3.0, (Double)res);
    }

    @Test
    public void testUnaryNegateWithBool() {
        final Expr e = new Expr.Unary(TokenFactory.make(TokenType.BANG), new Expr.Literal(true));
        final Interpreter i = new Interpreter(errorLogger);
        
        final Object res = i.interpret(e);

        assertNotNull(res);
        assertFalse((boolean)res);
    }
}
