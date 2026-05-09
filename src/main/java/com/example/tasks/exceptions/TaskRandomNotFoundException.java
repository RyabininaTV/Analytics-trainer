package com.example.tasks.exceptions;

import jakarta.ws.rs.WebApplicationException;

public class TaskRandomNotFoundException extends WebApplicationException {

    public TaskRandomNotFoundException() {
        super("Random task not found");
    }

}
