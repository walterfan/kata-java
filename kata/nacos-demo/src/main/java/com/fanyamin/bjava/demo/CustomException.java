package com.fanyamin.bjava.demo;

public class CustomException extends RuntimeException {
    private String customMessage;

    public CustomException(String message) {
        super(message);
        this.customMessage = message;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}