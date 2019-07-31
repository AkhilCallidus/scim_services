package com.calliduscloud.scas.scim_services.response.exception;

public class GroupNameAlreadyExistException extends Exception {
    private String errorCode;

    private String errorDescription;

    public GroupNameAlreadyExistException() {
        super();
    }

    public GroupNameAlreadyExistException(String message, Throwable cause, boolean enableSuppression,
                                          boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public GroupNameAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public GroupNameAlreadyExistException(String message) {
        super(message);
        this.errorDescription = message;
    }

    public GroupNameAlreadyExistException(String message, String errorCode) {
        super(message);
        this.errorDescription = message;
        this.errorCode = errorCode;
    }

    public GroupNameAlreadyExistException(Throwable cause) {
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
