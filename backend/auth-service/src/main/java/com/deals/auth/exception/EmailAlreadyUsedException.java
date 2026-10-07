package com.deals.auth.exception;

// Thrown when registering with an email that already has an account -> HTTP 409
public class EmailAlreadyUsedException extends RuntimeException {

    public EmailAlreadyUsedException(String email) {
        super("An account with email " + email + " already exists");
    }
}
