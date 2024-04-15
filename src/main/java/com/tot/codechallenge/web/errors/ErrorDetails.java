package com.tot.codechallenge.web.errors;

import org.springframework.http.HttpStatus;

/**
 * Represents the details of an error that can occur within the application.
 * This class holds information about the error state including the HTTP status,
 * a user-friendly message, and a more detailed description useful for debugging.
 */
public class ErrorDetails {

  HttpStatus httpStatus;
  String message;
  String description;

  public HttpStatus getHttpStatus() {
    return httpStatus;
  }

  public void setHttpStatus(HttpStatus httpStatus) {
    this.httpStatus = httpStatus;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Constructs an {@code ErrorDetails} object with specified details.
   *
   * @param httpStatus the HTTP status code associated with the error
   * @param message a user-friendly message describing the error
   * @param description a detailed description of the error, potentially for debugging purposes
   */
  public ErrorDetails(HttpStatus httpStatus, String message, String description) {
    this.httpStatus = httpStatus;
    this.message = message;
    this.description = description;
  }
}
