package com.calliduscloud.scas.scim_services.exception;

public class UserNameAlreadyExistException extends Exception {
    private String errorCode;

    private String errorDescription;

    public UserNameAlreadyExistException() {
        super();
    }

    public UserNameAlreadyExistException(String message, Throwable cause, boolean enableSuppression,
                                         boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public UserNameAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNameAlreadyExistException(String message) {
        super(message);
        this.errorDescription = message;
    }

    public UserNameAlreadyExistException(String message, String errorCode) {
        super(message);
        this.errorDescription = message;
        this.errorCode = errorCode;
    }

    public UserNameAlreadyExistException(Throwable cause) {
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
