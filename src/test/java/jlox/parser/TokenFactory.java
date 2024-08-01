package jlox.parser;

import jlox.scanner.Token;
import jlox.scanner.TokenLexemeResolver;
import jlox.scanner.TokenType;

/**
 * Helper class for creating tokens of certain type and abstracts some properties which are not important for some tests.
 */
public abstract class TokenFactory {
    public static Token make(TokenType type) {
        final String lexeme = TokenLexemeResolver.ttoc(type) + "";
        return new Token(type, lexeme, 1);
    }

    public static Token make(TokenType type, String lexeme) {
        return switch (type) {
            case TokenType.NUMBER -> new Token(type, lexeme, Double.parseDouble(lexeme), 1);        
            default -> new Token(type, lexeme, lexeme, 1);
        };
    }
}
