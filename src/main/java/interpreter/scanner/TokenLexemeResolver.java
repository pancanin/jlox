package interpreter.scanner;

import java.util.HashMap;
import java.util.Map;

/**
 * A helper class to evaluate between TokenType and char and vise-versa.
 */
public abstract class TokenLexemeResolver {
    private static Map<TokenType, Character> tokenTypeToCharMap;
    private static Map<Character, TokenType> charToTokenTypeMap;

    static {
        tokenTypeToCharMap = Map.ofEntries(
            Map.entry(TokenType.LEFT_PAREN, '('),
            Map.entry(TokenType.RIGHT_PAREN, ')'),
            Map.entry(TokenType.LEFT_BRACE, '{'),
            Map.entry(TokenType.RIGHT_BRACE, '}'),
            Map.entry(TokenType.COMMA, ','),
            Map.entry(TokenType.DOT, '.'),
            Map.entry(TokenType.MINUS, '-'),
            Map.entry(TokenType.PLUS, '+'),
            Map.entry(TokenType.SEMICOLON, ';'),
            Map.entry(TokenType.STAR, '*'),
            Map.entry(TokenType.BANG, '!'),
            Map.entry(TokenType.EQUAL, '='),
            Map.entry(TokenType.LESS, '<'),
            Map.entry(TokenType.GREATER, '>'),
            Map.entry(TokenType.SLASH, '/')
        );

        charToTokenTypeMap = new HashMap<>();

        for (Map.Entry<TokenType, Character> entry : tokenTypeToCharMap.entrySet()) {
            charToTokenTypeMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static Character ttoc(TokenType type) {
        return tokenTypeToCharMap.get(type);
    }

    public static TokenType ctot(char c) {
        return charToTokenTypeMap.get(c);
    }
}