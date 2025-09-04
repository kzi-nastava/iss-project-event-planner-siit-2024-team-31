package com.example.eventplanner.exception.handlers;

import com.example.eventplanner.dto.CommonMessageDTO;
import com.example.eventplanner.exception.exceptions.general.BadRequestException;
import com.example.eventplanner.exception.exceptions.general.ForbiddenException;
import com.example.eventplanner.exception.exceptions.general.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<CommonMessageDTO> handleForbiddenException(ForbiddenException ex) {
        return new ResponseEntity<>(new CommonMessageDTO(null, ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<CommonMessageDTO> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(new CommonMessageDTO(null, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<CommonMessageDTO> handleUnauthorizedException(UnauthorizedException ex) {
        return new ResponseEntity<>(new CommonMessageDTO(null, ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<CommonMessageDTO> handleAuthenticationException(AuthenticationException ex) {
        CommonMessageDTO commonMessageDTO = new CommonMessageDTO();
        commonMessageDTO.setMessage("Authentication failed. Please check your credentials.");
        commonMessageDTO.setError(ex.getMessage());
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<CommonMessageDTO> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        CommonMessageDTO commonMessageDTO = new CommonMessageDTO();
        commonMessageDTO.setMessage("Access denied. Insufficient permissions.");
        commonMessageDTO.setError(ex.getMessage());
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonMessageDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        CommonMessageDTO commonMessageDTO = new CommonMessageDTO();
        commonMessageDTO.setMessage("Invalid request parameters.");
        commonMessageDTO.setError(ex.getMessage());
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CommonMessageDTO> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        CommonMessageDTO commonMessageDTO = new CommonMessageDTO();
        commonMessageDTO.setMessage("Invalid parameter format.");
        commonMessageDTO.setError(ex.getMessage());
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonMessageDTO> handleException(Exception e) {
        CommonMessageDTO commonMessageDTO = new CommonMessageDTO();
        commonMessageDTO.setMessage("An error occurred. Please try again later.");
        commonMessageDTO.setError(e.getMessage());
        return new ResponseEntity<>(commonMessageDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
