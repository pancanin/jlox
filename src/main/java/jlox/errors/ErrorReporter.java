package jlox.errors;

import java.util.function.Consumer;

/**
 * Util for logging that can be configured with an output stream and a format for the message.
 */
public final class ErrorReporter {
    private String format = "[line %d] Error %s: %s";
    private Consumer<String> reporterFunc;

    public ErrorReporter() {
        this.reporterFunc = System.err::println;
    }

    public ErrorReporter(Consumer<String> reporterFunc) {
        this.reporterFunc = reporterFunc;
    }

    public void report(int line, String where, String msg) {
        reporterFunc.accept(String.format(format, line, where, msg));
    }
}
