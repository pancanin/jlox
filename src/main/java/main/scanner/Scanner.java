package main.scanner;

import java.util.ArrayList;
import java.util.List;

public class Scanner {
    private final String source;
    private final List<Token> tokens;

    public Scanner(String source) {
        this.source = source;
        this.tokens = new ArrayList<>();
    }
}
