package interpreter.errors;

import java.util.function.Consumer;

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
