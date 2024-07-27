package jlox.errors;

import java.util.function.Consumer;

/**
 * Utililty class for logging that can be configured with an output stream and a format for the message.
 */
public final class ErrorLogger {
    private String format = "[line %d] Error %s: %s";
    private Consumer<String> logFunc;

    public ErrorLogger() {
        this.logFunc = System.err::println;
    }

    public ErrorLogger(Consumer<String> reporterFunc) {
        this.logFunc = reporterFunc;
    }

    public void report(int line, String where, String msg) {
        logFunc.accept(String.format(format, line, where, msg));
    }
}
