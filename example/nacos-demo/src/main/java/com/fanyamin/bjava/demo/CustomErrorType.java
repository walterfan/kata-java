package com.fanyamin.bjava.demo;

public class CustomErrorType {
    private String error;
    private String message;
    private String details;

    public CustomErrorType(String error, String message) {
        this.error = error;
        this.message = message;
    }

    // 如果需要，可以添加更多的构造函数、getter和setter方法

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}