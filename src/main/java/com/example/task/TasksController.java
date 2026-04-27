package com.example.task;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.openapi.annotations.Operation;

import static com.example.task.utils.TasksEndpoints.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static lombok.AccessLevel.PRIVATE;

@Slf4j
@Path(BASE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TasksController {

    TasksService tasksService;

    @GET
    @Operation(summary = "Список всех заданий")
    public Response tasks() {
        return Response.status(OK)
                .entity(tasksService.getAll())
                .build();
    }

    @GET
    @Path(DETAILS)
    @Operation(summary = "Деталка задания")
    public Response details(@PathParam("id") long id) {
        return Response.status(OK)
                .entity(tasksService.getTask(id)
                        .orElseThrow(() -> {
                            log.error("Task not found with id: {}", id);
                            return new WebApplicationException("Task not found with id: " + id, Response.Status.NOT_FOUND);
                        }))
                .build();
    }

    @GET
    @Path(RANDOM)
    @Operation(summary = "Случайное задание")
    public Response details() {
        return Response.status(OK)
                .entity(tasksService.getRandom()
                        .orElseThrow(() -> {
                            log.error("Task not found with random id");
                            return new WebApplicationException("Task not found with random id", Response.Status.NOT_FOUND);
                        }))
                .build();
    }
}
