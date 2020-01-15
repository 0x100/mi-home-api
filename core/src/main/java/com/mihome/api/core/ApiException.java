package com.mihome.api.core;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }

    public ApiException(Exception ex) {
        super(ex);
    }
}
