package com.example.eventplanner.config;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotActivatedException.class)
    public ResponseEntity<CommonMessageDTO> handleUserNotActivatedException(UserNotActivatedException ex) {
        return new ResponseEntity<>(new CommonMessageDTO(null, ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ConfirmationExpirationException.class)
    public ResponseEntity<CommonMessageDTO> handleConfirmationExpirationException(ConfirmationExpirationException ex) {
        return new ResponseEntity<>(new CommonMessageDTO(null, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CommonMessageDTO> handleUserNotFoundException(UserNotFoundException ex) {
        return new ResponseEntity<>(new CommonMessageDTO(null, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailAlreadyUsedException.class)
    public ResponseEntity<CommonMessageDTO> handleEmailAlreadyUsedException(EmailAlreadyUsedException ex) {
        return new ResponseEntity<>(new CommonMessageDTO(null, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<CommonMessageDTO> handleInvalidTokenException(InvalidTokenException ex) {
        return new ResponseEntity<>(new CommonMessageDTO(null, ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonMessageDTO> handleException(Exception e) {
        CommonMessageDTO commonMessageDTO = new CommonMessageDTO();
        commonMessageDTO.setMessage("An error occurred. Please try again later.");
        commonMessageDTO.setError(e.getMessage());
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
