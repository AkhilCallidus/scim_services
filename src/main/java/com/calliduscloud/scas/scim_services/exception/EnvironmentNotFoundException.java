package com.calliduscloud.scas.scim_services.exception;

public class EnvironmentNotFoundException extends Exception {
    private static final long serialVersionUID = -3790961358744026820L;

    private String errorCode;

    private String errorDescription;

    public EnvironmentNotFoundException() {
        super();
    }

    public EnvironmentNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                        boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public EnvironmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EnvironmentNotFoundException(String message) {
        super(message);
        this.errorDescription = message;
    }

    public EnvironmentNotFoundException(String message, String errorCode) {
        super(message);
        this.errorDescription = message;
        this.errorCode = errorCode;
    }

    public EnvironmentNotFoundException(Throwable cause) {
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
