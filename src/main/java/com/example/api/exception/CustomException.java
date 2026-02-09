package com.example.api.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final String errorCode;
    private final int statusCode;

    public CustomException(String message) {
        super(message);
        this.errorCode = "INTERNAL_ERROR";
        this.statusCode = 500;
    }

    public CustomException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "INTERNAL_ERROR";
        this.statusCode = 500;
    }

}
