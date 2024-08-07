package jlox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import jlox.errors.ErrorLogger;
import jlox.errors.ParseError;
import jlox.errors.RuntimeError;
import jlox.interpreter.Interpreter;
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
        run(Files.readString(Paths.get(path)));

        if (hadError) System.exit(65);
    }

    private static void runPrompt() throws IOException {
        try (InputStreamReader input = new InputStreamReader(System.in)) {
            try (BufferedReader reader = new BufferedReader(input)) {
                for (;;) {
                    System.out.print("> ");
                    String line = reader.readLine();
                    if (line == null) break;
                    run(line);
        
                    // Reset this flag so that the error does not propagate to the next line.
                    hadError = false;
                }
            }
        }
    }

    private static void run(String source) {
        Scanner scanner = new Scanner(source, errorLogger);
        List<Token> tokens = scanner.scanTokens();
        Parser p = new Parser(tokens);
        Expr expr = p.parse();

        if (p.getError().notNull()) {
            ParseError err = p.getError().get();
            errorLogger.report(err.getToken().line, err.getToken().lexeme, err.getMessage());
            hadError = true;
            return;
        }

        Interpreter interpreter = new Interpreter();
        final Object res = interpreter.interpret(expr);

        if (interpreter.getError().notNull()) {
            RuntimeError err = interpreter.getError().get();
            errorLogger.report(err.getToken().line, err.getToken().lexeme, err.getMessage());
            hadError = true;
            return;
        }

        System.out.println(res.toString());
    }
}
