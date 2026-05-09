package com.example.admin.exceptions;

import jakarta.ws.rs.WebApplicationException;

public class TrainerWasNotCreatedException extends WebApplicationException {

    public TrainerWasNotCreatedException() {
        super("Не смог создать тренажер");
    }

}