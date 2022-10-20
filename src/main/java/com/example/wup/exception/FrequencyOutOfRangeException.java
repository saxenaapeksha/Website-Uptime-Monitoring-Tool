package com.example.wup.exception;

import org.springframework.http.HttpStatus;

public class FrequencyOutOfRangeException extends Exception {
    private String response;
    private HttpStatus status;

    public FrequencyOutOfRangeException(String errorMessage) {
        super(errorMessage);
    }
}
