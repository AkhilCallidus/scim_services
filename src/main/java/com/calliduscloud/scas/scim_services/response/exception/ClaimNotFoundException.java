package com.calliduscloud.scas.scim_services.response.exception;

public class ClaimNotFoundException extends Exception {
    private static final long serialVersionUID = -6305310317710672501L;

    private String errorCode;

    private String errorDescription;

    public ClaimNotFoundException() {
        super();
    }

    public ClaimNotFoundException(String message, Throwable cause, boolean enableSuppression,
                                  boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ClaimNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ClaimNotFoundException(String message) {
        super(message);
        this.errorDescription = message;
    }

    public ClaimNotFoundException(String message, String errorCode) {
        super(message);
        this.errorDescription = message;
        this.errorCode = errorCode;
    }

    public ClaimNotFoundException(Throwable cause) {
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
