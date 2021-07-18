package ru.polyan.onlinecart.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ServiceResponse {
    private boolean success;
    private String[] errors;

    public ServiceResponse(boolean success, String[] errors) {
        this.success = success;
        this.errors = errors;
    }

}
