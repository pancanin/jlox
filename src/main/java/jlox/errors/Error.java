package jlox.errors;

/**
 * To be composed in classes and give them with the ability to store information about errors that occured and retrieve it.
 */
public final class Error<ErrorType> {

    /**
     * If we have an error during parsing, this flag will be set to true and the 'error' field will contain the error.
     */
    private final boolean hasError;
    private final ErrorType error;

    private Error() {
        hasError = false;
        error = null;
    }

    public Error(ErrorType error) {
        this.hasError = true;
        this.error = error;
    }
    
    public boolean notNull() {
        return hasError;
    }

    public ErrorType get() {
        return error;
    }

    public static <ErrorType> Error<ErrorType> None() {
        return new Error<ErrorType>();
    }
}
