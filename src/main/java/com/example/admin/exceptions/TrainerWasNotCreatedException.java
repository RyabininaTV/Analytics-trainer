package com.example.admin.exceptions;

public class TrainerWasNotCreatedException extends RuntimeException {
    public TrainerWasNotCreatedException() {
        super("Не смог создать тренажер");
    }
}