package com.calliduscloud.scas.scim_services.response.exception;

public class InvalidGroupIdException extends Exception {
    private String errorCode;
    private String errorDescription;

    public InvalidGroupIdException(String message) {
        super(message);
        this.errorDescription = message;
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