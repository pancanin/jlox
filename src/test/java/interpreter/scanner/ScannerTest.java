package interpreter.scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import interpreter.errors.ErrorReporter;

class ScannerTest {

    static ErrorReporter errorReporter;

    static {
        errorReporter = new ErrorReporter(System.out::println);
    }

    @Test
    public void shouldParseSimpleTokensIgnoresComments() {
        String code = "// this is a comment\n" +
                "(( )){} // grouping stuff\n" +
                "!*+-/=<> <= == // operators";

        Scanner scanner = new Scanner(code, errorReporter);
        List<Token> tokens = scanner.scanTokens();

        assertEquals(tokens.get(0).type, TokenType.LEFT_PAREN);
        assertEquals(tokens.get(1).type, TokenType.LEFT_PAREN);
        assertEquals(tokens.get(2).type, TokenType.RIGHT_PAREN);
        assertEquals(tokens.get(3).type, TokenType.RIGHT_PAREN);
        assertEquals(tokens.get(4).type, TokenType.LEFT_BRACE);
        assertEquals(tokens.get(5).type, TokenType.RIGHT_BRACE);
        assertEquals(tokens.get(6).type, TokenType.BANG);
        assertEquals(tokens.get(7).type, TokenType.STAR);
        assertEquals(tokens.get(8).type, TokenType.PLUS);
        assertEquals(tokens.get(9).type, TokenType.MINUS);
        assertEquals(tokens.get(10).type, TokenType.SLASH);
        assertEquals(tokens.get(11).type, TokenType.EQUAL);
        assertEquals(tokens.get(12).type, TokenType.LESS);
        assertEquals(tokens.get(13).type, TokenType.GREATER);
        assertEquals(tokens.get(14).type, TokenType.LESS_EQUAL);
        assertEquals(tokens.get(15).type, TokenType.EQUAL_EQUAL);
    }

    @Test
    public void shouldConsumeGreedily() {
        String code = "=========";
        Scanner scanner = new Scanner(code, errorReporter);
        List<Token> tokens = scanner.scanTokens();
        assertEquals(6, tokens.size());
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(0).type);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(1).type);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(2).type);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(3).type);
        assertEquals(TokenType.EQUAL, tokens.get(4).type);
    }

    @Test
    public void shouldConsumeSimpleIfStatement() {
        String code = "if (12.23 == 12) { print(\"hi\"); }";
        Scanner scanner = new Scanner(code, errorReporter);
        List<Token> tokens = scanner.scanTokens();

        assertEquals(TokenType.IF, tokens.get(0).type);
        assertEquals(TokenType.LEFT_PAREN, tokens.get(1).type);
        assertEquals(TokenType.NUMBER, tokens.get(2).type);
        assertEquals(12.23, tokens.get(2).literal);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(3).type);
        assertEquals(TokenType.NUMBER, tokens.get(4).type);
        assertEquals(12.0, tokens.get(4).literal);
        assertEquals(TokenType.RIGHT_PAREN, tokens.get(5).type);
        assertEquals(TokenType.LEFT_BRACE, tokens.get(6).type);
        assertEquals(TokenType.PRINT, tokens.get(7).type);
        assertEquals(TokenType.LEFT_PAREN, tokens.get(8).type);
        assertEquals(TokenType.STRING, tokens.get(9).type);
        assertEquals("hi", tokens.get(9).literal);
        assertEquals(TokenType.RIGHT_PAREN, tokens.get(10).type);
        assertEquals(TokenType.SEMICOLON, tokens.get(11).type);
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(12).type);
    }

    @Test
    public void shouldParseStringLiterals() {
        String code = "\"Hello this is a string literal\"";
        Scanner sc = new Scanner(code, errorReporter);
        List<Token> tokens = sc.scanTokens();

        assertEquals(TokenType.STRING, tokens.get(0).type);
        assertEquals("Hello this is a string literal", tokens.get(0).literal);
        assertEquals("\"Hello this is a string literal\"", tokens.get(0).lexeme);
    }

    @Test
    public void shouldParseMultilineStringLiterals() {
        String code = "\"Hello this is a string\n" +
                " literal\"";
        Scanner sc = new Scanner(code, errorReporter);
        List<Token> tokens = sc.scanTokens();

        assertEquals(TokenType.STRING, tokens.get(0).type);
        assertEquals("Hello this is a string\n literal", tokens.get(0).literal);
        assertEquals("\"Hello this is a string\n literal\"", tokens.get(0).lexeme);
    }

    @Test
    public void shouldNotConsumeUnterminatedStrings() {
        String code = "\"Hello this is a string";
        Scanner sc = new Scanner(code, errorReporter);
        List<Token> tokens = sc.scanTokens();
        assertEquals(1, tokens.size());
        assertEquals(TokenType.EOF, tokens.get(0).type);
    }

    @Test
    public void shouldConsumeIntLiteral() {
        String code = "1234";
        Scanner sc = new Scanner(code, errorReporter);
        List<Token> tokens = sc.scanTokens();
        assertEquals(2, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals(1234.0, tokens.get(0).literal);
        assertEquals("1234", tokens.get(0).lexeme);
    }

    @Test
    public void shouldConsumeDoubleLiteral() {
        String code = "1234.56";
        Scanner sc = new Scanner(code, errorReporter);
        List<Token> tokens = sc.scanTokens();
        assertEquals(2, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals(1234.56, tokens.get(0).literal);
        assertEquals("1234.56", tokens.get(0).lexeme);
    }

    @Test
    public void shouldNotConsumeTrailingDecimalPointNumber() {
        String code = "1234.";
        Scanner sc = new Scanner(code, errorReporter);
        List<Token> tokens = sc.scanTokens();
        assertEquals(3, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals(1234.0, tokens.get(0).literal);
        assertEquals("1234", tokens.get(0).lexeme);
        assertEquals(TokenType.DOT, tokens.get(1).type);
    }

    @Test
    public void shouldConsumeIdentifiers() {
        String code = "_hello2 radius 00abc";
        Scanner sc = new Scanner(code, errorReporter);
        List<Token> tokens = sc.scanTokens();
        assertEquals(5, tokens.size());
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
        assertEquals("_hello2", tokens.get(0).lexeme);
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals("radius", tokens.get(1).lexeme);
        assertEquals(TokenType.NUMBER, tokens.get(2).type);
        assertEquals("00", tokens.get(2).lexeme);
        assertEquals(TokenType.IDENTIFIER, tokens.get(3).type);
        assertEquals("abc", tokens.get(3).lexeme);
    }

    @Test
    public void shouldConsumeKeywords() {
        String code = "var radius = abc;";
        Scanner sc = new Scanner(code, errorReporter);
        List<Token> tokens = sc.scanTokens();
        assertEquals(6, tokens.size());
        assertEquals(TokenType.VAR, tokens.get(0).type);
        assertEquals("var", tokens.get(0).lexeme);
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals("radius", tokens.get(1).lexeme);
        assertEquals(TokenType.EQUAL, tokens.get(2).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(3).type);
        assertEquals("abc", tokens.get(3).lexeme);
        assertEquals(TokenType.SEMICOLON, tokens.get(4).type);
    }

    @Test
    public void shouldIgnoreBlockComments() {
        String code = "var\n" +
                "/*\n" +
                "* This is a block comment\n" +
                "/* This is a nested /* This is hella nested code */ block comment */\n" +
                "*/\n" +
                        "a";
        Scanner sc = new Scanner(code, errorReporter);
        List<Token> tokens = sc.scanTokens();
        assertEquals(3, tokens.size());
        assertEquals(TokenType.VAR, tokens.get(0).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals(sc.getCurrentLine(), 6);
    }
}