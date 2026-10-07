package com.deals.coupon.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Turns exceptions thrown anywhere in a controller into clean JSON error responses.
 * ProblemDetail is the standard error format for HTTP APIs (RFC 9457).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CouponNotFoundException.class)
    public ProblemDetail handleNotFound(CouponNotFoundException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(DuplicateCouponCodeException.class)
    public ProblemDetail handleDuplicate(DuplicateCouponCodeException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Thrown by Spring when a @Valid request body breaks a validation rule
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Some fields are invalid");
        problem.setProperty("errors", fieldErrors);
        return problem;
    }
}
