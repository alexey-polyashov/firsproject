package ru.polyan.onlinecart.utils;

public class ServiceResponse {
    private boolean success;
    private String[] errors;

    public ServiceResponse(boolean success, String[] errors) {
        this.success = success;
        this.errors = errors;
    }

    public boolean isSuccess() {
        return success;
    }

    public String[] getErrors() {
        return errors;
    }
}
