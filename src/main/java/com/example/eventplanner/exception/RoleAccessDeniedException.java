package com.example.eventplanner.exception;

public class RoleAccessDeniedException extends AccessDeniedException {

    public RoleAccessDeniedException() {
        super();
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "YOU ROLE HAS NO PERMISSIONS FOR THIS. ";
    }
}
