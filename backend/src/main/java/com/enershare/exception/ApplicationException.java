package com.enershare.exception;

public class ApplicationException extends RuntimeException {

    private String code;

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public String getCode() {
        return code;
    }

}
