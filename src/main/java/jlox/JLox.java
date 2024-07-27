package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import jlox.errors.ErrorLogger;
import jlox.errors.ParseError;
import jlox.interpreter.Interpreter;
import jlox.parser.AstPrinter;
import jlox.parser.Expr;
import jlox.parser.Parser;
import jlox.scanner.Scanner;
import jlox.scanner.Token;

public class JLox {

    private static boolean hadError;

    private static ErrorLogger errorLogger;

    static {
        errorLogger = new ErrorLogger(System.out::println);
    }

    public static void main(String[] args) throws IOException {
        if (args.length > 1) {
            System.out.println("Usage: jlox [script]");
            System.exit(64);
        } else if (args.length == 1) {
            runFile(args[0]);
        } else {
            runPrompt();
        }
    }

    private static void runFile(String path) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(path));
        run(new String(bytes, Charset.defaultCharset()));

        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException {
        InputStreamReader input = new InputStreamReader(System.in);
        BufferedReader reader = new BufferedReader(input);

        for (;;) {
            System.out.print("> ");
            String line = reader.readLine();
            if (line == null) break;
            run(line);

            // Reset this flag so that the error does not propagate to the next line.
            hadError = false;
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source, errorLogger);
        List<Token> tokens = scanner.scanTokens();
        Parser p = new Parser(tokens);
        Expr expr = p.parse();

        if (p.hasError()) {
            ParseError err = p.getError();
            errorLogger.report(err.getToken().line, err.getToken().lexeme, err.getMessage());
            hadError = true;
            return;
        }

        Interpreter interpreter = new Interpreter(errorLogger);
        interpreter.interpret(expr);
    }
}
