package com.example.trainers;

import com.example.security.annotations.Secured;
import com.example.trainers.services.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;

import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.trainers.TrainerEndpoints.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class TrainerController {

    GetAllTrainersService getAllTrainersService;
    GetTrainerInfoByIdService getTrainerInfoByIdService;
    GetTrainerTasksByIdService getTrainerTasksByIdService;
    GetUserProgressByTrainerIdService getUserProgressByTrainerIdService;
    ResetUserProgressByTrainerIdService resetUserProgressByTrainerIdService;

    @GET
    @Path(GET_ALL_TRAINERS)
    @Operation(summary = "Получение списка всех тренажеров")
    public Response getAllTrainers() {
        return Response.ok(getAllTrainersService.getAllTrainers()).build();
    }

    @GET
    @Path(GET_TRAINER_INFO_BY_ID)
    @Operation(summary = "Получение информации о тренажере по ID")
    public Response getTrainerInfoById(@PathParam("id") long id) {
        return Response.ok(getTrainerInfoByIdService.getTrainerInfoById(id)).build();
    }

    @GET
    @Path(GET_TRAINER_TASKS_BY_ID)
    @Operation(summary = "Получение задач тренажера по ID")
    public Response getTrainerTasksById(@PathParam("id") long id) {
        return Response.ok(getTrainerTasksByIdService.getTrainerTasksById(id)).build();
    }

    @GET
    @Secured(roles = {USER, ADMIN})
    @Path(GET_USER_PROGRESS_BY_TRAINER_ID)
    @Operation(summary = "Получение прогресса пользователя по ID тренажера")
    public Response getUserProgressByTrainerId(@PathParam("id") long id) {
        return Response.ok(getUserProgressByTrainerIdService.getUserProgressByTrainerId(id)).build();
    }

    @POST
    @Secured(roles = {USER, ADMIN})
    @Path(RESET_USER_PROGRESS_BY_TRAINER_ID)
    @Operation(summary = "Сброс прогресса пользователя по ID тренажера")
    public Response resetUserProgressByTrainerId(@PathParam("id") long id) {
        resetUserProgressByTrainerIdService.resetUserProgressByTrainerId(id);
        return Response.noContent().build();
    }

}
