package com.example.admin;

import com.example.admin.dto.requests.CreateTaskRequest;
import com.example.admin.dto.requests.CreateTrainerRequest;
import com.example.admin.dto.requests.UpdateTaskRequest;
import com.example.admin.dto.requests.UpdateTrainerRequest;
import com.example.admin.services.*;
import com.example.security.annotations.Secured;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import static com.example.admin.AdminEndpoints.*;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminController {

    CreateTrainerService createTrainerService;
    UpdateTrainerService updateTrainerService;
    CreateTaskService createTaskService;
    UpdateTaskService updateTaskService;
    DeleteTaskService deleteTaskService;

    @POST
    @Path(CREATE_TRAINER)
    @Secured(roles = {ADMIN})
    @Operation(summary = "Создание нового тренажера")
    public Response createTrainer(@Valid @RequestBody CreateTrainerRequest request) {
        return Response.ok(createTrainerService.createTrainer(request)).build();
    }

    @PUT
    @Path(UPDATE_BY_ID)
    @Secured(roles = {ADMIN})
    @Operation(summary = "Редактирование тренажера")
    public Response updateTrainer(
            @PathParam("id") long id,
            @Valid @RequestBody UpdateTrainerRequest request
    ) {
        return Response.ok(updateTrainerService.updateTrainer(id, request)).build();
    }

    @POST
    @Path(CREATE_TASK)
    @Secured(roles = {ADMIN})
    @Operation(summary = "Создание нового задания")
    public Response createTask(@Valid @RequestBody CreateTaskRequest request) {
        return Response.ok(createTaskService.createTask(request)).build();
    }

    @PUT
    @Path(UPDATE_TASK)
    @Secured(roles = {ADMIN})
    @Operation(summary = "Редактирование задания")
    public Response updateTask(
            @PathParam("id") long id,
            @Valid @RequestBody UpdateTaskRequest request
    ) {
        return Response.ok(updateTaskService.updateTask(id, request)).build();
    }

    @DELETE
    @Path(DELETE_TASK)
    @Secured(roles = {ADMIN})
    @Operation(summary = "Удаление задания")
    public Response deleteTask(@PathParam("id") long id) {
        deleteTaskService.deleteTask(id);
        return Response.noContent().build();
    }

}