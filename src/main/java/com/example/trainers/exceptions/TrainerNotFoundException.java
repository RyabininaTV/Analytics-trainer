package com.example.trainers.exceptions;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

public class TrainerNotFoundException extends WebApplicationException {

    public TrainerNotFoundException(long id) {
        super("Trainer not found by id: " + id,  Response.Status.NOT_FOUND);
    }

}
