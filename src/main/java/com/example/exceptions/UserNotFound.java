package com.example.exceptions;

public class UserNotFound extends RuntimeException {
    public UserNotFound() {
        super("Пользователь не найден");
    }
}
