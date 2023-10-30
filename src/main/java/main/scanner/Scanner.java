package main.scanner;

import main.JLox;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens;

    private int start = 0; // indicates the start of a lexeme
    private int current = 0; // Where we are in the source code
    private int line = 1;

    public Scanner(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenType.LEFT_PAREN); break;
            case ')': addToken(TokenType.RIGHT_PAREN); break;
            case '{': addToken(TokenType.LEFT_BRACE); break;
            case '}': addToken(TokenType.RIGHT_BRACE); break;
            case ',': addToken(TokenType.COMMA); break;
            case '.': addToken(TokenType.DOT); break;
            case '-': addToken(TokenType.MINUS); break;
            case '+': addToken(TokenType.PLUS); break;
            case ';': addToken(TokenType.SEMICOLON); break;
            case '*': addToken(TokenType.STAR); break;
            case '!':
                addToken(match('=') ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match('=') ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match('=') ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match('=') ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '/':
                if (match('/')) {
                    // Comment
                    while (!isAtEnd() && peek() != '\n') advance();
                    break;
                }
                addToken(TokenType.SLASH);
                break;
            case '"':
                string();
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;
            case '\n':
                line++;
                break;
            default:
                if (Character.isDigit(c)) {
                    number();
                    break;
                }
                if (c == '_' || Character.isAlphabetic(c)) {
                    identifier();
                    break;
                }
                JLox.error(line, "Unexpected symbol: " + c);
                break;
        }
    }

    private void string() {
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            JLox.error(line, "Unterminated string");
            return;
        }

        advance();

        String literal = source.substring(start + 1, current - 1);
        addToken(TokenType.STRING, literal);
    }

    private void number() {
        while (!isAtEnd() && Character.isDigit(peek())) {
            advance();
        }

        if (!isAtEnd() && peek() == '.' && Character.isDigit(peekNext())) {
            advance();
            while (!isAtEnd() && Character.isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private void identifier() {
        while (!isAtEnd() && (peek() == '_' || Character.isAlphabetic(peek()) || Character.isDigit(peek()))) {
            advance();
        }

        addToken(TokenType.IDENTIFIER);
    }

    // Returns the char we are examining now and advances the 'current' cursor to the next character.
    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        addToken(type, null);
    }

    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private boolean match(char token) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != token) return false;
        current++;
        return true;
    }

    private char peek() {
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }
}
