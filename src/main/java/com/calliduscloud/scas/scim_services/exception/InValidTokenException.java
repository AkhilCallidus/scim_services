package com.calliduscloud.scas.scim_services.exception;

public class InValidTokenException extends Exception {

    public InValidTokenException() {
        super();
    }

    public InValidTokenException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InValidTokenException(String message, Throwable cause) {
        super(message, cause);
    }

    public InValidTokenException(String message) {
        super(message);
    }

    public InValidTokenException(Throwable cause) {
        super(cause);
    }
}