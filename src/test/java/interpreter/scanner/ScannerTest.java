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
        final String code = "// this is a comment\n" +
                "(( )){} // grouping stuff\n" +
                "!*+-/=<> <= == // operators";

        final Scanner scanner = new Scanner(code, errorReporter);
        final List<Token> tokens = scanner.scanTokens();

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
        final String code = "=========";
        final Scanner scanner = new Scanner(code, errorReporter);
        final List<Token> tokens = scanner.scanTokens();
        assertEquals(6, tokens.size());
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(0).type);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(1).type);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(2).type);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(3).type);
        assertEquals(TokenType.EQUAL, tokens.get(4).type);
    }

    @Test
    public void shouldConsumeSimpleIfStatement() {
        final String code = "if (12.23 == 12) { print(\"hi\"); }";
        final Scanner scanner = new Scanner(code, errorReporter);
        final List<Token> tokens = scanner.scanTokens();

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
        final String code = "\"Hello this is a string literal\"";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();

        assertEquals(TokenType.STRING, tokens.get(0).type);
        assertEquals("Hello this is a string literal", tokens.get(0).literal);
        assertEquals("\"Hello this is a string literal\"", tokens.get(0).lexeme);
    }

    @Test
    public void shouldParseMultilineStringLiterals() {
        final String code = "\"Hello this is a string\n" +
                " literal\"";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();

        assertEquals(TokenType.STRING, tokens.get(0).type);
        assertEquals("Hello this is a string\n literal", tokens.get(0).literal);
        assertEquals("\"Hello this is a string\n literal\"", tokens.get(0).lexeme);
    }

    @Test
    public void shouldNotConsumeUnterminatedStrings() {
        final String code = "\"Hello this is a string";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();
        assertEquals(1, tokens.size());
        assertEquals(TokenType.EOF, tokens.get(0).type);
    }

    @Test
    public void shouldConsumeIntLiteral() {
        final String code = "1234";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();
        assertEquals(2, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals(1234.0, tokens.get(0).literal);
        assertEquals("1234", tokens.get(0).lexeme);
    }

    @Test
    public void shouldConsumeDoubleLiteral() {
        final String code = "1234.56";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();
        assertEquals(2, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals(1234.56, tokens.get(0).literal);
        assertEquals("1234.56", tokens.get(0).lexeme);
    }

    @Test
    public void shouldNotConsumeTrailingDecimalPointNumber() {
        final String code = "1234.";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();
        assertEquals(3, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals(1234.0, tokens.get(0).literal);
        assertEquals("1234", tokens.get(0).lexeme);
        assertEquals(TokenType.DOT, tokens.get(1).type);
    }

    @Test
    public void shouldConsumeIdentifiers() {
        // 00abc is ignored as an identifier as they cannot start with a number.
        final String code = "_hello2 radius 00abc";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();
        assertEquals(3, tokens.size());
        assertEquals(TokenType.IDENTIFIER, tokens.get(0).type);
        assertEquals("_hello2", tokens.get(0).lexeme);
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals("radius", tokens.get(1).lexeme);
    }

    @Test
    public void shouldConsumeKeywords() {
        final String code = "var radius = abc;";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();
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
    public void shouldConsumeForCycle() {
        final String code = "for (var i = 0; i < 10; i++) {}";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();
        assertEquals(18, tokens.size());
        final Token token1 = tokens.get(0);
        assertEquals(TokenType.FOR, token1.type);

        final Token token2 = tokens.get(1);
        assertEquals(TokenType.LEFT_PAREN, token2.type);

        final Token token3 = tokens.get(2);
        assertEquals(TokenType.VAR, token3.type);

        final Token token4 = tokens.get(3);
        assertEquals(TokenType.IDENTIFIER, token4.type);
        assertEquals("i", token4.lexeme);

        final Token token5 = tokens.get(4);
        assertEquals(TokenType.EQUAL, token5.type);

        final Token token6 = tokens.get(5);
        assertEquals(TokenType.NUMBER, token6.type);
        assertEquals("0", token6.lexeme);

        final Token token7 = tokens.get(6);
        assertEquals(TokenType.SEMICOLON, token7.type);

        final Token token8 = tokens.get(7);
        assertEquals(TokenType.IDENTIFIER, token8.type);
        assertEquals("i", token8.lexeme);

        final Token token9 = tokens.get(8);
        assertEquals(TokenType.LESS, token9.type);

        final Token token10 = tokens.get(9);
        assertEquals(TokenType.NUMBER, token10.type);
        assertEquals("10", token10.lexeme);

        final Token token11 = tokens.get(10);
        assertEquals(TokenType.SEMICOLON, token11.type);

        final Token token12 = tokens.get(11);
        assertEquals(TokenType.IDENTIFIER, token12.type);
        assertEquals("i", token12.lexeme);

        final Token token13 = tokens.get(12);
        assertEquals(TokenType.PLUS, token13.type);

        final Token token14 = tokens.get(13);
        assertEquals(TokenType.PLUS, token14.type);

        final Token token15 = tokens.get(14);
        assertEquals(TokenType.RIGHT_PAREN, token15.type);

        final Token token16 = tokens.get(15);
        assertEquals(TokenType.LEFT_BRACE, token16.type);

        final Token token17 = tokens.get(16);
        assertEquals(TokenType.RIGHT_BRACE, token17.type);

        final Token token18 = tokens.get(17);
        assertEquals(TokenType.EOF, token18.type);
    }

    @Test
    public void shouldConsumeForCycleNoSpaces() {
        final String code = "for(var i=0;i<10;i++){}";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();
        assertEquals(18, tokens.size());
        final Token token1 = tokens.get(0);
        assertEquals(TokenType.FOR, token1.type);

        final Token token2 = tokens.get(1);
        assertEquals(TokenType.LEFT_PAREN, token2.type);

        final Token token3 = tokens.get(2);
        assertEquals(TokenType.VAR, token3.type);

        final Token token4 = tokens.get(3);
        assertEquals(TokenType.IDENTIFIER, token4.type);
        assertEquals("i", token4.lexeme);

        final Token token5 = tokens.get(4);
        assertEquals(TokenType.EQUAL, token5.type);

        final Token token6 = tokens.get(5);
        assertEquals(TokenType.NUMBER, token6.type);
        assertEquals("0", token6.lexeme);

        final Token token7 = tokens.get(6);
        assertEquals(TokenType.SEMICOLON, token7.type);

        final Token token8 = tokens.get(7);
        assertEquals(TokenType.IDENTIFIER, token8.type);
        assertEquals("i", token8.lexeme);

        final Token token9 = tokens.get(8);
        assertEquals(TokenType.LESS, token9.type);

        final Token token10 = tokens.get(9);
        assertEquals(TokenType.NUMBER, token10.type);
        assertEquals("10", token10.lexeme);

        final Token token11 = tokens.get(10);
        assertEquals(TokenType.SEMICOLON, token11.type);

        final Token token12 = tokens.get(11);
        assertEquals(TokenType.IDENTIFIER, token12.type);
        assertEquals("i", token12.lexeme);

        final Token token13 = tokens.get(12);
        assertEquals(TokenType.PLUS, token13.type);

        final Token token14 = tokens.get(13);
        assertEquals(TokenType.PLUS, token14.type);

        final Token token15 = tokens.get(14);
        assertEquals(TokenType.RIGHT_PAREN, token15.type);

        final Token token16 = tokens.get(15);
        assertEquals(TokenType.LEFT_BRACE, token16.type);

        final Token token17 = tokens.get(16);
        assertEquals(TokenType.RIGHT_BRACE, token17.type);

        final Token token18 = tokens.get(17);
        assertEquals(TokenType.EOF, token18.type);
    }

    @Test
    public void shouldIgnoreBlockComments() {
        final String code = "var\n" +
                "/*\n" +
                "* This is a block comment\n" +
                "/* This is a nested /* This is hella nested code */ block comment */\n" +
                "*/\n" +
                        "a";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();
        assertEquals(3, tokens.size());
        assertEquals(TokenType.VAR, tokens.get(0).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals(sc.getCurrentLine(), 6);
    }

    @Test
    public void shouldConsumeIdentifierStartingWithVar() {
        final String code = "var varco = \"var\"";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();

        assertEquals(5, tokens.size());

        assertEquals(TokenType.VAR, tokens.get(0).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(1).type);
        assertEquals("varco", tokens.get(1).lexeme);
        assertEquals(TokenType.EQUAL, tokens.get(2).type);
        assertEquals(TokenType.STRING, tokens.get(3).type);
        assertEquals("var", tokens.get(3).literal);
    }

    @Test
    public void shouldConsumeWhileTrueWithIfIfElseElse() {
        final String code = "while (true) {" +
                            "// Intern dev code commencing...\n" +
                            "   if (somevar == true) {" +
                            "       return true;" +
                            "   }" +
                            "   else if (somevar == false) {" +
                            "       return false;" +
                            "   }" +
                            "   else {" +
                            "       return nil == this;" +
                            "   }" +
                            "}";
        final Scanner sc = new Scanner(code, errorReporter);
        final List<Token> tokens = sc.scanTokens();

        assertEquals(38, tokens.size());

        assertEquals(TokenType.WHILE, tokens.get(0).type);
        assertEquals(TokenType.LEFT_PAREN, tokens.get(1).type);
        assertEquals(TokenType.TRUE, tokens.get(2).type);
        assertEquals(TokenType.RIGHT_PAREN, tokens.get(3).type);
        assertEquals(TokenType.LEFT_BRACE, tokens.get(4).type);
        assertEquals(TokenType.IF, tokens.get(5).type);
        assertEquals(TokenType.LEFT_PAREN, tokens.get(6).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(7).type);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(8).type);
        assertEquals(TokenType.TRUE, tokens.get(9).type);
        assertEquals(TokenType.RIGHT_PAREN, tokens.get(10).type);
        assertEquals(TokenType.LEFT_BRACE, tokens.get(11).type);
        assertEquals(TokenType.RETURN, tokens.get(12).type);
        assertEquals(TokenType.TRUE, tokens.get(13).type);
        assertEquals(TokenType.SEMICOLON, tokens.get(14).type);
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(15).type);
        assertEquals(TokenType.ELSE, tokens.get(16).type);
        assertEquals(TokenType.IF, tokens.get(17).type);
        assertEquals(TokenType.LEFT_PAREN, tokens.get(18).type);
        assertEquals(TokenType.IDENTIFIER, tokens.get(19).type);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(20).type);
        assertEquals(TokenType.FALSE, tokens.get(21).type);
        assertEquals(TokenType.RIGHT_PAREN, tokens.get(22).type);
        assertEquals(TokenType.LEFT_BRACE, tokens.get(23).type);
        assertEquals(TokenType.RETURN, tokens.get(24).type);
        assertEquals(TokenType.FALSE, tokens.get(25).type);
        assertEquals(TokenType.SEMICOLON, tokens.get(26).type);
        assertEquals(TokenType.RIGHT_BRACE, tokens.get(27).type);
        assertEquals(TokenType.ELSE, tokens.get(28).type);
        assertEquals(TokenType.LEFT_BRACE, tokens.get(29).type);
        assertEquals(TokenType.RETURN, tokens.get(30).type);
        assertEquals(TokenType.NIL, tokens.get(31).type);
        assertEquals(TokenType.EQUAL_EQUAL, tokens.get(32).type);
        assertEquals(TokenType.THIS, tokens.get(33).type);
    }
}
