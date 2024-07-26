package interpreter.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interpreter.errors.ErrorReporter;

public class Scanner {
    private final String source;
    private final List<Token> tokens;

    private final ErrorReporter errorReporter;

    private int start = 0; // indicates the start of a lexeme
    private int current = 0; // Where we are in the source code
    private int line = 1;

    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    TokenType.AND);
        keywords.put("class",  TokenType.CLASS);
        keywords.put("else",   TokenType.ELSE);
        keywords.put("false",  TokenType.FALSE);
        keywords.put("for",    TokenType.FOR);
        keywords.put("fun",    TokenType.FUN);
        keywords.put("if",     TokenType.IF);
        keywords.put("nil",    TokenType.NIL);
        keywords.put("or",     TokenType.OR);
        keywords.put("print",  TokenType.PRINT);
        keywords.put("return", TokenType.RETURN);
        keywords.put("super",  TokenType.SUPER);
        keywords.put("this",   TokenType.THIS);
        keywords.put("true",   TokenType.TRUE);
        keywords.put("var",    TokenType.VAR);
        keywords.put("while",  TokenType.WHILE);
    }

    public Scanner(String source, ErrorReporter errorReporter) {
        this.source = source;
        this.tokens = new ArrayList<>();
        this.errorReporter = errorReporter;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    public int getCurrentLine() {
        return line;
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
                if (match('*')) {
                    // Block comment
                    consumeBlockComment();
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
                errorReporter.report(line, c + "", String.format("Unexpected symbol '%c'", c));
                break;
        }
    }

    private void string() {
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            errorReporter.report(line, "", "Unterminated string");
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

        String lexeme = source.substring(start, current);
        addToken(keywords.getOrDefault(lexeme, TokenType.IDENTIFIER));
    }

    private void consumeBlockComment() {
        while (!(peekNext() == '/' && peek() == '*')) {
            if (peekNext() == '*' && peek() == '/') {
                advance();
                advance();
                consumeBlockComment();
            } else {
                if (isAtEnd()) break;
                if (peek() == '\n') line++;
                advance();
            }
        }
        if (!isAtEnd()) {
            advance();
            advance();
        }
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
        if (isAtEnd() || source.charAt(current) != token) return false;
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
