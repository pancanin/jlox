package main.parser;
import main.scanner.Token;

import java.util.List;

abstract class Expr {

  abstract <R> R accept(Visitor<R> visitor);
interface Visitor<R> {
	R visitLiteralExpr(Literal expr);
	R visitGroupingExpr(Grouping expr);
	R visitBinaryExpr(Binary expr);
	R visitUnaryExpr(Unary expr);
}
static class Literal extends Expr {
	Literal(Object value) {
		this.value = value;
	}

	@Override	<R> R accept(Visitor<R> visitor) {	return visitor.visitLiteralExpr(this);	}
	final Object value;
}

static class Grouping extends Expr {
	Grouping(Expr expression) {
		this.expression = expression;
	}

	@Override	<R> R accept(Visitor<R> visitor) {	return visitor.visitGroupingExpr(this);	}
	final Expr expression;
}

static class Binary extends Expr {
	Binary(Expr left, Token operator, Expr right) {
		this.left = left;
		this.operator = operator;
		this.right = right;
	}

	@Override	<R> R accept(Visitor<R> visitor) {	return visitor.visitBinaryExpr(this);	}
	final Expr left;
	final Token operator;
	final Expr right;
}

static class Unary extends Expr {
	Unary(Token operator, Expr right) {
		this.operator = operator;
		this.right = right;
	}

	@Override	<R> R accept(Visitor<R> visitor) {	return visitor.visitUnaryExpr(this);	}
	final Token operator;
	final Expr right;
}

}
