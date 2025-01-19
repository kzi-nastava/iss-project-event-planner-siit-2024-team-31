package com.example.eventplanner.exception;

public class AccessDeniedException extends RuntimeException {

    public AccessDeniedException() {
        super("ACCESS DENIED. ");
    }

}
