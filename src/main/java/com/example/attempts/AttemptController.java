package com.example.attempts;

import com.example.attempts.dto.requests.SubmitAttemptRequest;
import com.example.attempts.services.*;
import com.example.security.annotations.Secured;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;

import static com.example.attempts.AttemptEndpoints.*;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AttemptController {

    GetUserAttemptsService getUserAttemptsService;
    GetAttemptByTaskIdService getAttemptByTaskIdService;
    GetAttemptDetailsService getAttemptDetailsService;
    SubmitAttemptService submitAttemptService;

    @GET
    @Secured(roles = {USER, ADMIN})
    @Path(GET_USER_ATTEMPTS)
    @Operation(summary = "Получение истории всех попыток пользователя")
    public Response getUserAttempts() {
        return Response.ok(getUserAttemptsService.getUserAttempts()).build();
    }

    @GET
    @Secured(roles = {USER, ADMIN})
    @Path(GET_ATTEMPT_BY_TASK_ID)
    @Operation(summary = "Получение попытки по конкретному заданию")
    public Response getAttemptByTaskId(@PathParam("taskId") long taskId) {
        return Response.ok(getAttemptByTaskIdService.getAttemptByTaskId(taskId)).build();
    }

    @GET
    @Secured(roles = {USER, ADMIN})
    @Path(GET_ATTEMPT_DETAILS)
    @Operation(summary = "Детали попытки (включая ответ)")
    public Response getAttemptDetails(@PathParam("id") long id) {
        return Response.ok(getAttemptDetailsService.getAttemptDetails(id)).build();
    }

    @POST
    @Secured(roles = {USER, ADMIN})
    @Path(SUBMIT_ATTEMPT)
    @Operation(summary = "Отправка ответа на задание")
    public Response submitAttempt(SubmitAttemptRequest request) {
        return Response.ok(submitAttemptService.submitAttempt(request)).build();
    }
}
