package br.com.eztest.dcv;

public class ExpectationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ExpectationException(final String message) {
        super(message);
    }

    public ExpectationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ExpectationException(final Throwable cause) {
        super(cause);
    }

}
