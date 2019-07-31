package com.calliduscloud.scas.scim_services.response.exception;

public class UserAlreadyExistException extends Exception {
    private String errorCode;

    private String errorDescription;

    public UserAlreadyExistException() {
        super();
    }

    public UserAlreadyExistException(String message, Throwable cause, boolean enableSuppression,
                                     boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserAlreadyExistException(String message) {
        super(message);
        this.errorDescription = message;
    }

    public UserAlreadyExistException(String message, String errorCode) {
        super(message);
        this.errorDescription = message;
        this.errorCode = errorCode;
    }

    public UserAlreadyExistException(Throwable cause) {
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
