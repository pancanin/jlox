package jlox.parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import jlox.scanner.Token;
import jlox.scanner.TokenType;

class AstPrinterTest {

    @Test
    public void testAstPrinter() {
        // (1 + 2) * -10
        Expr.Literal one = new Expr.Literal(1);
        Expr.Literal two = new Expr.Literal(2);
        Expr.Binary addExpr = new Expr.Binary(one, new Token(TokenType.PLUS, "+", "+", 1), two);
        Expr.Literal ten = new Expr.Literal(10);
        Expr.Unary minTen = new Expr.Unary(new Token(TokenType.MINUS, "-", "-", 1), ten);
        Expr.Binary wholeExpr = new Expr.Binary(addExpr, new Token(TokenType.STAR, "*", "*", 1), minTen);

        AstPrinter printer = new AstPrinter();

        Assertions.assertEquals("(* (+ 1 2) (- 10))", printer.print(wholeExpr));
    }
}