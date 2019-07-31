package com.calliduscloud.scas.scim_services.response.exception;

public class InvalidClientDetailsException extends Exception {
    private static final long serialVersionUID = 7433070043871733308L;

    private String errorCode;

    private String errorDescription;

    public InvalidClientDetailsException() {
        super();
    }

    public InvalidClientDetailsException(String message, Throwable cause, boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public InvalidClientDetailsException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidClientDetailsException(String message) {
        super(message);
        this.errorDescription = message;
    }

    public InvalidClientDetailsException(String message, String errorCode) {
        super(message);
        this.errorDescription = message;
        this.errorCode = errorCode;
    }

    public InvalidClientDetailsException(Throwable cause) {
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
