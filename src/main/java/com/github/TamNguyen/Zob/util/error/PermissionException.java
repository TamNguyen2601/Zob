package com.github.TamNguyen.Zob.util.error;

public class PermissionException extends RuntimeException {
    // Constructor that accepts a message
    public PermissionException(String message) {
        super(message);
    }
}
