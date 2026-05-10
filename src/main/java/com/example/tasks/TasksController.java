package com.example.tasks;

import com.example.security.annotations.Secured;
import com.example.tasks.dto.responses.GetRandomTaskResponse;
import com.example.tasks.dto.responses.GetTaskDetailsResponse;
import com.example.tasks.dto.responses.GetTaskItemResponse;
import com.example.tasks.service.GetRandomTaskService;
import com.example.tasks.service.GetTaskDetailsService;
import com.example.tasks.service.GetTasksService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import static com.example.constants.BaseEndpoints.BASE_TASKS;
import static com.example.constants.HttpStatuses.OK;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;
import static org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY;

@Path(BASE_TASKS)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TasksController {

    public static final String TASK_DETAILS = "/{id}";
    public static final String RANDOM = "/random";

    GetTasksService getTasksService;
    GetTaskDetailsService getTaskDetailsService;
    GetRandomTaskService getRandomTaskService;

    @GET
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение списка всех заданий по тренажеру")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(
                    type = ARRAY,
                    implementation = GetTaskItemResponse.class
            ))
    )
    public Response getTasks(@QueryParam("trainer_id") Long trainerId) {
        return Response.ok(getTasksService.getTasks(trainerId)).build();
    }

    @GET
    @Path(TASK_DETAILS)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение деталей конкретного задания")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = GetTaskDetailsResponse.class))
    )
    public Response getTaskDetails(@PathParam("id") long id) {
        return Response.ok(getTaskDetailsService.getTaskDetails(id)).build();
    }

    @GET
    @Path(RANDOM)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение случайного задания")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = GetRandomTaskResponse.class))
    )
    public Response getRandomTask() {
        return Response.ok(getRandomTaskService.getRandomTask()).build();
    }

}
