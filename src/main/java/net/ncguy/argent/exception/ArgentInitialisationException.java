package net.ncguy.argent.exception;

/**
 * Created by Guy on 17/06/2016.
 */
public class ArgentInitialisationException extends Exception {

    public ArgentInitialisationException() {
    }

    public ArgentInitialisationException(String message) {
        super(message);
    }

    public ArgentInitialisationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArgentInitialisationException(Throwable cause) {
        super(cause);
    }

    public ArgentInitialisationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
