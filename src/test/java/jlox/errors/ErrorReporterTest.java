package jlox.errors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

class ErrorReporterTest {

    @Test
    void reportShouldDisplayWithCorrectFormat() {
        int line = 13;
        String where = "???";
        String msg = "Unexpected token";
        Consumer<String> consumer = (String s) -> {
            Assertions.assertEquals("[line 13] Error ???: Unexpected token", s);
        };
        ErrorReporter reporter = new ErrorReporter(consumer);
        reporter.report(line, where, msg);
    }
}
