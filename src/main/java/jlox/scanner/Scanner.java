package jlox.scanner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jlox.errors.ErrorReporter;

/**
 * Turns source code into a list of tokens.
 * @author Valeri Hristov (valericfbg@gmail.com)
 */
public class Scanner {
    private final String source;
    private final List<Token> tokens;

    private final ErrorReporter errorReporter;

    /**
     * Used to keep track of the start of a lexeme and then using this index to extract the whole lexeme from the source code.
     */
    private int start = 0;

    /**
     * Used to keep track of where we are in the source code.
     */
    private int current = 0;

    /**
     * Used to keep track of which line we have reached in the source code.
     * This applies to running a script file and is later used to report on which line an error happened.
     */
    private int line = 1;

    /**
     * A map between text representation of a keyword, as it would appear in the code, and an enum type of the token.
     * The token type is easier to work with later in the Parser and Interpreter.
     */
    public static final Map<String, TokenType> keywords;

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

    /**
     * Entrypoint of the Scanner class.
     * @return A list of tokens, generated from the source code.
     */
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

    /**
     * Are we at the end of the source code, meaning that there are no more characters to consume.
     * @return true if we are at the end of the source code.
     */
    private boolean isAtEnd() {
        return current >= source.length();
    }

    /**
     * Looks at the current character and either consumes just it, if it's a single character token or consumes a sequence of characters in the case of
     * strings, numbers, etc.
     */
    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(TokenLexemeResolver.ctot('(')); break;
            case ')': addToken(TokenLexemeResolver.ctot(')')); break;
            case '{': addToken(TokenLexemeResolver.ctot('{')); break;
            case '}': addToken(TokenLexemeResolver.ctot('}')); break;
            case ',': addToken(TokenLexemeResolver.ctot(',')); break;
            case '.': addToken(TokenLexemeResolver.ctot('.')); break;
            case '-': addToken(TokenLexemeResolver.ctot('-')); break;
            case '+': addToken(TokenLexemeResolver.ctot('+')); break;
            case ';': addToken(TokenLexemeResolver.ctot(';')); break;
            case '*': addToken(TokenLexemeResolver.ctot('*')); break;
            case '!':
                addToken(match(TokenLexemeResolver.ttoc(TokenType.EQUAL)) ? TokenType.BANG_EQUAL : TokenType.BANG);
                break;
            case '=':
                addToken(match(TokenLexemeResolver.ttoc(TokenType.EQUAL)) ? TokenType.EQUAL_EQUAL : TokenType.EQUAL);
                break;
            case '<':
                addToken(match(TokenLexemeResolver.ttoc(TokenType.EQUAL)) ? TokenType.LESS_EQUAL : TokenType.LESS);
                break;
            case '>':
                addToken(match(TokenLexemeResolver.ttoc(TokenType.EQUAL)) ? TokenType.GREATER_EQUAL : TokenType.GREATER);
                break;
            case '/':
                if (match(TokenLexemeResolver.ttoc(TokenType.SLASH))) {
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

    /**
     * Consumes a string, which is a character sequence between double quotes.
     */
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

    /**
     * Consumes a number, either integer or floating point.
     * The underlying storage type is Double.
     */
    private void number() {
        while (!isAtEnd() && Character.isDigit(peek())) {
            advance();
            if (!isAtEnd() && Character.isAlphabetic(peek())) {
                errorReporter.report(line, "", "Unexpected alphabetic character in number literal: " + peek());
                consumeUntilSpace();
                return;
            }
        }

        if (!isAtEnd() && peek() == '.' && Character.isDigit(peekNext())) {
            advance();
            while (!isAtEnd() && Character.isDigit(peek())) advance();
        }

        addToken(TokenType.NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    /**
     * Consumes an identifier.
     * Identifiers can start with an underscore, alphabetic or digit.
     * TODO: Hmm, it is strange that they can start with a digit...check this.
     */
    private void identifier() {
        while (!isAtEnd() && (peek() == '_' || Character.isAlphabetic(peek()) || Character.isDigit(peek()))) {
            advance();
        }

        String lexeme = source.substring(start, current);
        addToken(keywords.getOrDefault(lexeme, TokenType.IDENTIFIER));
    }

    /**
     * Moves beyond a block comment. Quoted example: "\* something *\". In effect, ignores block comments.
     */
    private void consumeBlockComment() {
        while (!(peekNext() == '/' && peek() == '*')) {
            if (peekNext() == '*' && peek() == '/') {
                advance();
                advance();
                // Consume nested block comments.
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

    /**
     * Gets the current character in the source code and advances to the next character.
     * @return The char in the source code which is pointed by 'current' index.
     */
    private char advance() {
        return source.charAt(current++);
    }

    /**
     * Adds a token without a literal value.
     * @param type The token type.
     */
    private void addToken(TokenType type) {
        addToken(type, null);
    }

    /**
     * Adds a token to the list of tokens with a literal value. Extracts the string representation of the value from the source code.
     * @param type The token type. Could be identifier, string, number, etc.
     * @param literal The name of the identifier, for example or the value of the integer or string variable.
     */
    private void addToken(TokenType type, Object literal) {
        final String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    /**
     * Checks if the current character in the source code equals the argument.
     * If it is not equal, or if we are at the end of the source code, then there is no match.
     * If they are equal, advance the source code cursor.
     * @param token The character that we search in the source code.
     * @return true if the argument matches the current character in the source code.
     */
    private boolean match(char token) {
        if (isAtEnd() || peek() != token) return false;
        current++;
        return true;
    }

    /**
     * Just reads the current character in the source code.
     * @return The current character in the source code.
     */
    private char peek() {
        return source.charAt(current);
    }

    /**
     * Reads the character after the current one in the source code. Used for look-aheads.
     * If we are at the last character now, then it returns a null-terminated char.
     * @return The next character in the source code.
     */
    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private void consumeUntilSpace() {
        while (!isAtEnd() && peek() != ' ') {
            advance();
        }
    }
}
