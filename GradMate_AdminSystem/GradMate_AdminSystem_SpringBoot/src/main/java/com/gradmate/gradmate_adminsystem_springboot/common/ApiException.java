package com.gradmate.gradmate_adminsystem_springboot.common;

public class ApiException extends RuntimeException {
    public ApiException(String message) {
        super(message);
    }
}
