package main;

import main.errors.ErrorReporter;
import main.parser.AstPrinter;
import main.parser.Expr;
import main.parser.ParseError;
import main.parser.Parser;
import main.scanner.Scanner;
import main.scanner.Token;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JLox {

    private static boolean hadError;

    private static ErrorReporter errorReporter;

    static {
        errorReporter = new ErrorReporter(System.out::println);
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
        Scanner scanner = new Scanner(source, errorReporter);
        List<Token> tokens = scanner.scanTokens();
        Parser p = new Parser(tokens);
        Expr expr = p.parse();

        if (p.hasError()) {
            ParseError err = p.getError();
            errorReporter.report(err.getToken().line, err.getToken().lexeme, err.getMsg());
            hadError = true;
            return;
        }

        System.out.println(new AstPrinter().print(expr));
    }
}
