package com.calliduscloud.scas.scim_services.exception;

public class JwtTokenEmptyException extends Exception {
    private static final long serialVersionUID = 1303338549165755132L;

    private String errorCode;

    private String errorDescription;

    public JwtTokenEmptyException() {
        super();
    }

    public JwtTokenEmptyException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public JwtTokenEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public JwtTokenEmptyException(String message) {
        super(message);
        this.errorDescription = message;
    }

    public JwtTokenEmptyException(String message, String errorCode) {
        super(message);
        this.errorDescription = message;
        this.errorCode = errorCode;
    }

    public JwtTokenEmptyException(Throwable cause) {
        super(cause);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }
}
