package com.example.trainers;

import com.example.security.annotations.Secured;
import com.example.trainers.dto.responses.TaskByTrainerIdResponse;
import com.example.trainers.dto.responses.TrainerInfoByIdResponse;
import com.example.trainers.dto.responses.TrainerResponse;
import com.example.trainers.dto.responses.UserProgressByTrainerIdResponse;
import com.example.trainers.services.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import static com.example.constants.BaseEndpoints.*;
import static com.example.constants.HttpStatuses.NO_CONTENT;
import static com.example.constants.HttpStatuses.OK;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;
import static org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY;

@Path(BASE_TRAINERS)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TrainerController {

    public static final String GET_TRAINER_INFO_BY_ID = "/{id}";
    public static final String GET_TRAINER_TASKS_BY_ID = "/{id}" + BASE_TASKS;
    public static final String GET_USER_PROGRESS_BY_TRAINER_ID = "/{id}" + BASE_PROGRESS;
    public static final String RESET_USER_PROGRESS_BY_TRAINER_ID = "/{id}/reset";

    GetAllTrainersService getAllTrainersService;
    GetTrainerInfoByIdService getTrainerInfoByIdService;
    GetTrainerTasksByIdService getTrainerTasksByIdService;
    GetUserProgressByTrainerIdService getUserProgressByTrainerIdService;
    ResetUserProgressByTrainerIdService resetUserProgressByTrainerIdService;

    @GET
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение списка всех тренажеров")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(
                    type = ARRAY,
                    implementation = TrainerResponse.class
            ))
    )
    public Response getAllTrainers() {
        return Response.ok(getAllTrainersService.getAllTrainers()).build();
    }

    @GET
    @Secured(roles = {USER, ADMIN})
    @Path(GET_TRAINER_INFO_BY_ID)
    @Operation(summary = "Получение информации о тренажере по ID")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = TrainerInfoByIdResponse.class))
    )
    public Response getTrainerInfoById(@PathParam("id") long id) {
        return Response.ok(getTrainerInfoByIdService.getTrainerInfoById(id)).build();
    }

    @GET
    @Secured(roles = {USER, ADMIN})
    @Path(GET_TRAINER_TASKS_BY_ID)
    @Operation(summary = "Получение задач тренажера по ID")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(
                    type = ARRAY,
                    implementation = TaskByTrainerIdResponse.class
            ))
    )
    public Response getTrainerTasksById(@PathParam("id") long id) {
        return Response.ok(getTrainerTasksByIdService.getTrainerTasksById(id)).build();
    }

    @GET
    @Secured(roles = {USER, ADMIN})
    @Path(GET_USER_PROGRESS_BY_TRAINER_ID)
    @Operation(summary = "Получение прогресса пользователя по ID тренажера")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = UserProgressByTrainerIdResponse.class))
    )
    public Response getUserProgressByTrainerId(@PathParam("id") long id) {
        return Response.ok(getUserProgressByTrainerIdService.getUserProgressByTrainerId(id)).build();
    }

    @POST
    @Secured(roles = {USER, ADMIN})
    @Path(RESET_USER_PROGRESS_BY_TRAINER_ID)
    @Operation(summary = "Сброс прогресса пользователя по ID тренажера")
    @APIResponse(responseCode = NO_CONTENT)
    public Response resetUserProgressByTrainerId(@PathParam("id") long id) {
        resetUserProgressByTrainerIdService.resetUserProgressByTrainerId(id);
        return Response.noContent().build();
    }

}
