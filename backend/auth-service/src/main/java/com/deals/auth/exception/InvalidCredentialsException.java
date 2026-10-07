package com.deals.auth.exception;

// Thrown when email or password is wrong -> HTTP 401.
// The message never says WHICH one was wrong, so attackers can't use it to discover registered emails.
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
