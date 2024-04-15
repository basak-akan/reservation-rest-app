package com.tot.codechallenge.web.errors;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.http.MediaType;

/**
 * Centralized exception handling across all {@code @RequestMapping} methods through {@code @ExceptionHandler} methods.
 * This class provides global error handling and is annotated with {@code @ControllerAdvice} to be considered a controller advice where
 * exception handler methods are defined.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handles {@link BadRequestException} instances thrown from any controller within the application.
   * This method creates a {@link ResponseEntity} with an {@link ErrorDetails} response body.
   *
   * @param ex the exception caught
   * @param request the web request during which the exception was raised
   * @return a {@link ResponseEntity} object containing the {@link ErrorDetails} and HTTP status code
   */
  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(false));
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(errorDetails);
  }

  /**
   * Handles {@link IllegalArgumentException} instances thrown from any controller within the application.
   * This method creates a {@link ResponseEntity} with an {@link ErrorDetails} response body.
   *
   * @param ex the exception caught
   * @param request the web request during which the exception was raised
   * @return a {@link ResponseEntity} object containing the {@link ErrorDetails} and HTTP status code
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(false));
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_JSON)
        .body(errorDetails);
  }

  /**
   * Handles all other {@link Exception} instances that do not have specific handlers.
   * This method acts as a fallback for all other exceptions, creating a {@link ResponseEntity} with an {@link ErrorDetails} response body.
   *
   * @param ex the exception caught
   * @param request the web request during which the exception was raised
   * @return a {@link ResponseEntity} object containing the {@link ErrorDetails} and HTTP status code
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error occurred", request.getDescription(false));
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(errorDetails);
  }
}
