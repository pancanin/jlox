package jlox.parser;

/**
 * A bit of thoughs on the Visitor pattern.
 * Normally, in an OOP language we would implement methods directly on the data (Expr), for example, each Expr type will be able to interpret itself.
 * That seems sensible, but what if we add a new type of operation. Then we have to go through all classes and add this method.
 * 
 * In functional languages, we would have the data and functions separate. The functions would use pattern-matching to do different things for different types.
 * But if we add a new type, we would have to go through all the functions and add a case for the new type.
 * 
 * We need a pattern that makes adding new types (Expr) easy and defining new actions (interpret, analyze, etc) easy too.
 * 
 * The solution: Visitor pattern
 * 
 * abstract class Expr {}
 * 
  class Binary extends Expr {}

  class Unary extends Expr {}

  We want to define new operations on the expressions without modifying any of the classes.
  
  For this we define a new interface:
  interface ExprVisitor {
    void visitBinary(Binary b);
    void visitUnary(Unary u);
  }

  New operations on the Expr is a new class implementing the ExprVisitor interface.

  interface Interpreter extends ExprVisitor {
    void visitBinary(Binary b) { // Evaluate the result of the binary expression. }
    ...
  }

  How do we connect the operation and the type?

  Each type of Expr will have a method that accepts a Visitor.

  class Binary extends Expr {
    void accept(ExprVisitor v) {
        v.visitBinary(this);
    }
  }

  The visitor that we pass to the accept method will have the functionality that we want to execute, for example, interpret the expression.
  The visitor below can show a string representation of the AST, it's return type is String. But in the interpreter, the return type could be some other value. 
 */
public final class AstPrinter implements Expr.Visitor<String> {

    public String print(Expr expr) {
        return expr.accept(this);
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        return expr.value.toString();
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr);
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append('(').append(name);

        for (Expr e : exprs) {
            strBuilder.append(' ').append(e.accept(this));;
        }
        strBuilder.append(')');

        return strBuilder.toString();
    }
}
