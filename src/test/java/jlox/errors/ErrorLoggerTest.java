package jlox.errors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

class ErrorLoggerTest {

    @Test
    void reportShouldDisplayWithCorrectFormat() {
        int line = 13;
        String where = "???";
        String msg = "Unexpected token";
        Consumer<String> consumer = (String s) -> {
            Assertions.assertEquals("[line 13] Error ???: Unexpected token", s);
        };
        ErrorLogger reporter = new ErrorLogger(consumer);
        reporter.report(line, where, msg);
    }
}
