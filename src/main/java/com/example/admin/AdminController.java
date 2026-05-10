package com.example.admin;

import com.example.admin.dto.requests.CreateTaskRequest;
import com.example.admin.dto.requests.CreateTrainerRequest;
import com.example.admin.dto.requests.UpdateTaskRequest;
import com.example.admin.dto.requests.UpdateTrainerRequest;
import com.example.admin.dto.responses.CreateTaskResponse;
import com.example.admin.dto.responses.CreateTrainerResponse;
import com.example.admin.dto.responses.UpdateTaskResponse;
import com.example.admin.dto.responses.UpdateTrainerResponse;
import com.example.admin.services.*;
import com.example.security.annotations.Secured;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import static com.example.constants.BaseEndpoints.*;
import static com.example.constants.HttpStatuses.*;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE_ADMIN)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminController {

    public static final String UPDATE_TRAINER = BASE_TRAINERS + "/{id}";
    public static final String UPDATE_TASK = BASE_TASKS + "/{id}";
    public static final String DELETE_TASK = UPDATE_TASK;

    CreateTrainerService createTrainerService;
    UpdateTrainerService updateTrainerService;
    CreateTaskService createTaskService;
    UpdateTaskService updateTaskService;
    DeleteTaskService deleteTaskService;

    @POST
    @Path(BASE_TRAINERS)
    @Secured(roles = {ADMIN})
    @Operation(summary = "Создание нового тренажера")
    @APIResponse(
            responseCode = CREATED,
            content = @Content(schema = @Schema(implementation = CreateTrainerResponse.class))
    )
    public Response createTrainer(@Valid @RequestBody CreateTrainerRequest request) {
        return Response.status(Integer.parseInt(CREATED))
                .entity(createTrainerService.createTrainer(request))
                .build();
    }

    @PUT
    @Path(UPDATE_TRAINER)
    @Secured(roles = {ADMIN})
    @Operation(summary = "Редактирование тренажера")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = UpdateTrainerResponse.class))
    )
    public Response updateTrainer(
            @PathParam("id") long id,
            @Valid @RequestBody UpdateTrainerRequest request
    ) {
        return Response.ok(updateTrainerService.updateTrainer(id, request)).build();
    }

    @POST
    @Path(BASE_TASKS)
    @Secured(roles = {ADMIN})
    @Operation(summary = "Создание нового задания")
    @APIResponse(
            responseCode = CREATED,
            content = @Content(schema = @Schema(implementation = CreateTaskResponse.class))
    )
    public Response createTask(@Valid @RequestBody CreateTaskRequest request) {
        return Response.status(Integer.parseInt(CREATED))
                .entity(createTaskService.createTask(request))
                .build();
    }

    @PUT
    @Path(UPDATE_TASK)
    @Secured(roles = {ADMIN})
    @Operation(summary = "Редактирование задания")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = UpdateTaskResponse.class))
    )
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
    @APIResponse(responseCode = NO_CONTENT)
    public Response deleteTask(@PathParam("id") long id) {
        deleteTaskService.deleteTask(id);
        return Response.noContent().build();
    }

}