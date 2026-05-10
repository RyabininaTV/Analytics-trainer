package com.example.attempts;

import com.example.attempts.dto.requests.SubmitAttemptRequest;
import com.example.attempts.dto.responses.AttemptDetailsResponse;
import com.example.attempts.dto.responses.AttemptResponse;
import com.example.attempts.dto.responses.SubmitAttemptResponse;
import com.example.attempts.services.*;
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

import static com.example.constants.BaseEndpoints.BASE_ATTEMPTS;
import static com.example.constants.HttpStatuses.OK;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;
import static org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY;

@Path(BASE_ATTEMPTS)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AttemptController {

    public static final String GET_ATTEMPT_BY_TASK_ID = "/{taskId}";
    public static final String GET_ATTEMPT_DETAILS = "/{id}/details";

    GetUserAttemptsService getUserAttemptsService;
    GetAttemptByTaskIdService getAttemptByTaskIdService;
    GetAttemptDetailsService getAttemptDetailsService;
    SubmitAttemptService submitAttemptService;

    @GET
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение истории всех попыток пользователя")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(
                    type = ARRAY,
                    implementation = AttemptResponse.class
            ))
    )
    public Response getUserAttempts() {
        return Response.ok(getUserAttemptsService.getUserAttempts()).build();
    }

    @GET
    @Secured(roles = {USER, ADMIN})
    @Path(GET_ATTEMPT_BY_TASK_ID)
    @Operation(summary = "Получение всех попыток по конкретному заданию")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(
                    type= ARRAY,
                    implementation = AttemptResponse.class
            ))
    )
    public Response getAttemptsByTaskId(@PathParam("taskId") long taskId) {
        return Response.ok(getAttemptByTaskIdService.getAttemptsByTaskId(taskId)).build();
    }

    @GET
    @Secured(roles = {USER, ADMIN})
    @Path(GET_ATTEMPT_DETAILS)
    @Operation(summary = "Детали попытки (включая ответ)")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = AttemptDetailsResponse.class))
    )
    public Response getAttemptDetails(@PathParam("id") long id) {
        return Response.ok(getAttemptDetailsService.getAttemptDetails(id)).build();
    }

    @POST
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Отправка ответа на задание")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = SubmitAttemptResponse.class))
    )
    public Response submitAttempt(@Valid @RequestBody SubmitAttemptRequest request) {
        return Response.ok(submitAttemptService.submitAttempt(request)).build();
    }

}
