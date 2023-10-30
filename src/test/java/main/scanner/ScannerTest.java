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
}