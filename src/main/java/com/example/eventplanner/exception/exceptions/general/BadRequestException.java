package com.example.eventplanner.exception.exceptions.general;

public class BadRequestException extends RuntimeException {
  public BadRequestException(String message) {
    super(message);
  }
}
