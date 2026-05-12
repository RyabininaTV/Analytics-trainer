package com.example.progress_and_profile.controllers;

import com.example.progress_and_profile.dto.responses.ProgressHistoryItemResponse;
import com.example.progress_and_profile.dto.responses.ProgressItemResponse;
import com.example.progress_and_profile.dto.responses.ProgressResponse;
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
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import static com.example.constants.BaseEndpoints.BASE_PROGRESS;
import static com.example.constants.BaseEndpoints.BASE_TRAINERS;
import static com.example.constants.HttpStatuses.OK;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;
import static org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY;

@Path(BASE_PROGRESS)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProgressController {

    public static final String HISTORY = "/histrory";

    GetUserProgressByAllTrainersService getUserProgressByAllTrainersService;
    GetUserProgressService getUserProgressService;
    GetHistoryOfUserProgressChangesService getHistoryOfUserProgressChangesService;

    @GET
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение общего прогресса пользователя")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = ProgressResponse.class))
    )
    public Response getUserProgress() {
        return Response.ok(getUserProgressService.getUserProgress()).build();
    }

    @GET
    @Path(BASE_TRAINERS)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Прогресс пользователя по всем тренажерам")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(
                    type = ARRAY,
                    implementation = ProgressItemResponse.class
            ))
    )
    public Response getUserProgressByAllTrainers() {
        return Response.ok(getUserProgressByAllTrainersService.getUserProgressByAllTrainers()).build();
    }

    @GET
    @Path(HISTORY)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "История изменения прогресса пользователя")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(
                    type = ARRAY,
                    implementation = ProgressHistoryItemResponse.class
            ))
    )
    public Response getHistoryOfUserProgressChanges() {
        return Response.ok(getHistoryOfUserProgressChangesService.getHistoryOfUserProgressChanges()).build();
    }

}
