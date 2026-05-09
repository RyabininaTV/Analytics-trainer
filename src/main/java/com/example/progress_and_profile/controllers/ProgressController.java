package com.example.progress_and_profile.controllers;

import com.example.progress_and_profile.services.GetHistoryOfUserProgressChangesService;
import com.example.progress_and_profile.services.GetUserProgressService;
import com.example.progress_and_profile.services.GetUserProgressByAllTrainersService;
import com.example.security.annotations.Secured;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;

import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.progress_and_profile.endpoints.ProgressEndpoints.*;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProgressController {

    GetUserProgressByAllTrainersService getUserProgressByAllTrainersService;
    GetUserProgressService getUserProgressService;
    GetHistoryOfUserProgressChangesService getHistoryOfUserProgressChangesService;

    @GET
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение общего прогресса пользователя")
    public Response getUserProgress() {
        return Response.ok(getUserProgressService.getUserProgress()).build();
    }

    @GET
    @Path(TRAINERS)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Прогресс пользователя по всем тренажерам")
    public Response getUserProgressByAllTrainers() {
        return Response.ok(getUserProgressByAllTrainersService.getUserProgressByAllTrainers()).build();
    }

    @GET
    @Path(HISTORY)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "История изменения прогресса пользователя")
    public Response getHistoryOfUserProgressChanges() {
        return Response.ok(getHistoryOfUserProgressChangesService.getHistoryOfUserProgressChanges()).build();
    }

}
