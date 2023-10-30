package main.scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScannerTest {
    @Test
    public void shouldParseSimpleTokensIgnoresComments() {
        String code = "// this is a comment\n" +
                "(( )){} // grouping stuff\n" +
                "!*+-/=<> <= == // operators";

        Scanner scanner = new Scanner(code);
        List<Token> tokens = scanner.scanTokens();

        Assertions.assertEquals(tokens.get(0).type, TokenType.LEFT_PAREN);
        Assertions.assertEquals(tokens.get(1).type, TokenType.LEFT_PAREN);
        Assertions.assertEquals(tokens.get(2).type, TokenType.RIGHT_PAREN);
        Assertions.assertEquals(tokens.get(3).type, TokenType.RIGHT_PAREN);
        Assertions.assertEquals(tokens.get(4).type, TokenType.LEFT_BRACE);
        Assertions.assertEquals(tokens.get(5).type, TokenType.RIGHT_BRACE);
        Assertions.assertEquals(tokens.get(6).type, TokenType.BANG);
        Assertions.assertEquals(tokens.get(7).type, TokenType.STAR);
        Assertions.assertEquals(tokens.get(8).type, TokenType.PLUS);
        Assertions.assertEquals(tokens.get(9).type, TokenType.MINUS);
        Assertions.assertEquals(tokens.get(10).type, TokenType.SLASH);
        Assertions.assertEquals(tokens.get(11).type, TokenType.EQUAL);
        Assertions.assertEquals(tokens.get(12).type, TokenType.LESS);
        Assertions.assertEquals(tokens.get(13).type, TokenType.GREATER);
        Assertions.assertEquals(tokens.get(14).type, TokenType.LESS_EQUAL);
        Assertions.assertEquals(tokens.get(15).type, TokenType.EQUAL_EQUAL);
    }

    @Test
    public void shouldParseStringLiterals() {
        String code = "\"Hello this is a string literal\"";
        Scanner sc = new Scanner(code);
        List<Token> tokens = sc.scanTokens();

        Assertions.assertEquals(TokenType.STRING, tokens.get(0).type);
        Assertions.assertEquals("Hello this is a string literal", tokens.get(0).literal);
        Assertions.assertEquals("\"Hello this is a string literal\"", tokens.get(0).lexeme);
    }

    @Test
    public void shouldParseMultilineStringLiterals() {
        String code = "\"Hello this is a string\n" +
                " literal\"";
        Scanner sc = new Scanner(code);
        List<Token> tokens = sc.scanTokens();

        Assertions.assertEquals(TokenType.STRING, tokens.get(0).type);
        Assertions.assertEquals("Hello this is a string\n literal", tokens.get(0).literal);
        Assertions.assertEquals("\"Hello this is a string\n literal\"", tokens.get(0).lexeme);
    }

    @Test
    public void shouldNotConsumeUnterminatedStrings() {
        String code = "\"Hello this is a string";
        Scanner sc = new Scanner(code);
        List<Token> tokens = sc.scanTokens();
        assertEquals(1, tokens.size());
        assertEquals(TokenType.EOF, tokens.get(0).type);
    }

    @Test
    public void shouldConsumeIntLiteral() {
        String code = "1234";
        Scanner sc = new Scanner(code);
        List<Token> tokens = sc.scanTokens();
        assertEquals(2, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals(1234.0, tokens.get(0).literal);
        assertEquals("1234", tokens.get(0).lexeme);
    }

    @Test
    public void shouldConsumeDoubleLiteral() {
        String code = "1234.56";
        Scanner sc = new Scanner(code);
        List<Token> tokens = sc.scanTokens();
        assertEquals(2, tokens.size());
        assertEquals(TokenType.NUMBER, tokens.get(0).type);
        assertEquals(1234.56, tokens.get(0).literal);
        assertEquals("1234.56", tokens.get(0).lexeme);
    }

    @Test
    public void shouldNotConsumeTrailingDecimalPointNumber() {
        String code = "1234.";
        Scanner sc = new Scanner(code);
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
        Scanner sc = new Scanner(code);
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
}
