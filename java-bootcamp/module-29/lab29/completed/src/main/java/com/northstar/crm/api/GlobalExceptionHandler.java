package com.northstar.crm.api;

import com.northstar.crm.dto.ErrorResponse;
import com.northstar.crm.dto.ErrorResponse.FieldViolation;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
    // Finished prompt: convert field errors to the existing envelope shape.
    List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
        .map(error -> new FieldViolation(error.getField(), error.getDefaultMessage()))
        .sorted(Comparator.comparing(FieldViolation::getField).thenComparing(FieldViolation::getMessage))
        .toList();
    return respond(HttpStatus.BAD_REQUEST, "Validation failed", request, violations);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex,
      HttpServletRequest request) {
    return respond(HttpStatus.BAD_REQUEST, "Malformed request body", request, List.of());
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleNotFound(IllegalArgumentException ex, HttpServletRequest request) {
    // Finished prompt: the starter service uses this exception for a missing customer.
    return respond(HttpStatus.NOT_FOUND, "Customer not found", request, List.of());
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<ErrorResponse> handleConflict(IllegalStateException ex, HttpServletRequest request) {
    // Finished prompt: the starter service uses this exception for duplicate IDs.
    return respond(HttpStatus.CONFLICT, "Duplicate customer", request, List.of());
  }

  @ExceptionHandler(ResponseStatusException.class)
  public ResponseEntity<ErrorResponse> handleStatus(ResponseStatusException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
    if (status == null) {
      status = HttpStatus.INTERNAL_SERVER_ERROR;
    }
    String message = status == HttpStatus.UNAUTHORIZED ? "Invalid credentials" : "Request failed";
    return respond(status, message, request, List.of());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleSafe500(Exception ex, HttpServletRequest request) {
    // Finished prompt: keep diagnostics server-side and hide them from the client.
    log.error("Unhandled CRM request failure", ex);
    return respond(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error", request, List.of());
  }

  private ResponseEntity<ErrorResponse> respond(HttpStatus status, String message,
      HttpServletRequest request, List<FieldViolation> violations) {
    ErrorResponse body = new ErrorResponse();
    body.setStatus(status.value());
    body.setError(status.getReasonPhrase());
    body.setMessage(message);
    String correlationId = request.getHeader("X-Correlation-Id");
    body.setCorrelationId(correlationId == null || correlationId.isBlank() ? "lab-request-001" : correlationId);
    body.setViolations(violations);
    return ResponseEntity.status(status).body(body);
  }
}
