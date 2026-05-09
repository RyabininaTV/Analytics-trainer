package com.example.tasks;

import com.example.security.annotations.Secured;
import com.example.tasks.service.GetRandomTaskService;
import com.example.tasks.service.GetTaskDetailsService;
import com.example.tasks.service.GetTasksService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;

import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.tasks.TasksEndpoints.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TasksController {

    GetTasksService getTasksService;
    GetTaskDetailsService getTaskDetailsService;
    GetRandomTaskService getRandomTaskService;

    @GET
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение списка всех заданий по тренажеру")
    public Response getTasks(@QueryParam("trainer_id") Long trainerId) {
        return Response.ok(getTasksService.getTasks(trainerId)).build();
    }

    @GET
    @Path(TASK_DETAILS)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение деталей конкретного задания")
    public Response getTaskDetails(@PathParam("id") long id) {
        return Response.ok(getTaskDetailsService.getTaskDetails(id)).build();
    }

    @GET
    @Path(RANDOM)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение случайного задания")
    public Response getRandomTask() {
        return Response.ok(getRandomTaskService.getRandomTask()).build();
    }

}
