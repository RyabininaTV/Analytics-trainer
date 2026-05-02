package com.example.task.presentation;

import com.example.task.domain.service.TasksService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;

import static com.example.task.utils.TasksEndpoints.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.OK;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TasksController {

    TasksService tasksService;

    @GET
    @Operation(summary = "Список всех заданий")
    public Response getAllTasks() {
        return Response.status(OK).entity(tasksService.getAllTasks()).build();
    }

    @GET
    @Path(DETAILS)
    @Operation(summary = "Получение деталей задачи по id")
    public Response getTaskDetailsById(@PathParam("id") long id) {
        return Response.status(OK).entity(tasksService.getTaskDetailsById(id)).build();
    }

    @GET
    @Path(RANDOM)
    @Operation(summary = "Получение деталей случайной задачи")
    public Response getTaskDetailsByRandomId() {
        return Response.status(OK).entity(tasksService.getTaskDetailsByRandomId()).build();
    }
}
