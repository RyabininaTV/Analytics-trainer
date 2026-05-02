package com.example.auth.exceptions;

public class CurrentUserIsNotSetException extends IllegalStateException {

    public CurrentUserIsNotSetException() {
        super("Current user is not set");
    }

}
