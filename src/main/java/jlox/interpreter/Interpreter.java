package jlox.interpreter;

import jlox.errors.ErrorReporter;
import jlox.errors.RuntimeError;
import jlox.parser.Expr;
import jlox.parser.Expr.Binary;
import jlox.parser.Expr.Grouping;
import jlox.parser.Expr.Literal;
import jlox.parser.Expr.Unary;
import jlox.scanner.Token;
import jlox.scanner.TokenType;

/**
 * Evaluates AST and produces a value or side effects.
 */
public class Interpreter implements Expr.Visitor<Object> {
    private final ErrorReporter errorReporter;

    public Interpreter(ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    public Object interpret(Expr expr) {
        try {
            final Object res = evaluate(expr);
            return res;
        } catch (RuntimeError e) {
            errorReporter.report(e.getToken().line, null, e.getMessage());
            return null;
        }
    }

    @Override
    public Object visitLiteralExpr(Literal expr) {
        return expr.value;
    }

    @Override
    public Object visitGroupingExpr(Grouping expr) {
        return evaluate(expr.expression);
    }

    @Override
    public Object visitBinaryExpr(Binary expr) {
        final Object leftVal = evaluate(expr.left);
        final Object rightVal = evaluate(expr.right);

        // Some of the operators can work only for numbers.
        switch (expr.operator.type) {
            case TokenType.MINUS:
            case TokenType.SLASH:
            case TokenType.STAR:
            case TokenType.LESS:
            case TokenType.LESS_EQUAL:
            case TokenType.GREATER:
            case TokenType.GREATER_EQUAL:
                checkNumberOperand(expr.operator, leftVal);
                checkNumberOperand(expr.operator, rightVal);
                break;
            default:
                break;
        }

        switch (expr.operator.type) {
            case TokenType.PLUS:
                return performBinaryPlus(expr.operator, leftVal, rightVal);
            case TokenType.MINUS:
                return (Double)leftVal - (Double)rightVal;
            case TokenType.SLASH:
                return (Double)leftVal / (Double)rightVal;
            case TokenType.STAR:
                return (Double)leftVal * (Double)rightVal;
            case TokenType.LESS:
                return (Double)leftVal < (Double)rightVal;
            case TokenType.LESS_EQUAL:
                return (Double)leftVal <= (Double)rightVal;
            case TokenType.GREATER:
                return (Double)leftVal > (Double)rightVal;
            case TokenType.GREATER_EQUAL:
                return (Double)leftVal >= (Double)rightVal;
            case TokenType.EQUAL_EQUAL:
                return isEqual(leftVal, rightVal);
            case TokenType.BANG_EQUAL:
                return !isEqual(leftVal, rightVal);
            default:
                break;
        }

        throw new RuntimeError(expr.operator, "Unimplemented binary operator.");
    }

    @Override
    public Object visitUnaryExpr(Unary expr) {
        final Object val = evaluate(expr.right);

        switch (expr.operator.type) {
            case TokenType.PLUS:
                checkNumberOperand(expr.operator, val);
                return val;
            case TokenType.MINUS:
                checkNumberOperand(expr.operator, val);
                return -(Double)val;
            case TokenType.BANG:
                checkBoolOperand(expr.operator, val);
                return !isTruthy(val);
            default:
                break;
        }

        throw new RuntimeError(expr.operator, "Unimplemented unary operator.");
    }

    private Object evaluate(Expr e) {
        return e.accept(this);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkBoolOperand(Token operator, Object operand) {
        if (operand instanceof Boolean || operand == null) return;
        throw new RuntimeError(operator, "Operand must be a boolean.");
    }

    private boolean isTruthy(Object val) {
        if (val == null) return false;
        if (val instanceof Boolean) return (Boolean)val;
        return true;
    }

    /**
     * When the left value is a string and the right value is a number, concatenate the stringified number to the left string.
     * When the left value is a string and the right value is a string, concatenate.
     * When the left value is a number and the right value is a string, throw error.
     * When the left value is a number and the right is a number, add them.
     * In other cases, throw an error.
     * @param leftVal The left-hand side of the binary expression.
     * @param rightVal THe right-hand side of the binary expression.
     * @return Returns the result of performing the + operation on the two values.
     */
    private Object performBinaryPlus(Token plus, Object leftVal, Object rightVal) {
        final boolean leftIsString = leftVal instanceof String;
        final boolean leftIsNumber = leftVal instanceof Double;
        final boolean rightIsString = rightVal instanceof String;
        final boolean rightIsNumber = rightVal instanceof Double;

        if (leftIsString && rightIsNumber) {
            return String.format("%s%d", leftVal, rightVal);
        }
        else if (leftIsString && rightIsString) {
            return (String)leftVal + rightVal;
        }
        else if (leftIsNumber && rightIsString) {
            throw new RuntimeError(plus, "Invalid operation between left-hand side number and right-hand side string");
        }
        else if (leftIsNumber && rightIsNumber) {
            return (Double)leftVal + (Double)rightVal;
        }
        else {
            final String msg = String.format("Unsupported operation between values: '%s' and '%s'.", leftVal, rightVal);
            throw new RuntimeError(plus, msg);
        }
    }

    private boolean isEqual(Object o1, Object o2) {
        if (o1 == null && o2 == null) return true;
        if (o1 == null) return false;
        return o1.equals(o2);
    }
}
