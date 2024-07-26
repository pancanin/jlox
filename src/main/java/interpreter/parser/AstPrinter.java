package interpreter.parser;

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
