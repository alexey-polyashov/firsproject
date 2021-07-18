package ru.polyan.onlinecart.utils;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse {
    private boolean success;
    private Object response;
    private String[] errors;
}
