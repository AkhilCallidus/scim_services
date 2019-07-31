package com.calliduscloud.scas.scim_services.response.exception;

public class ItemNotFoundException extends Exception {

    private static final long serialVersionUID = 7718828512143293558L;

    public ItemNotFoundException() {
        super();
    }

    public ItemNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                 boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(Throwable cause) {
        super(cause);
    }
}
