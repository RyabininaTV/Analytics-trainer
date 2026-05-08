package com.example.admin;

import com.example.admin.dto.requests.CreateTrainerRequest;
import com.example.admin.dto.responses.CreateTrainerResponse;
import com.example.admin.services.CreateTrainerService;
import com.example.security.annotations.Secured;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.trainers.AdminEndpoints.BASE_ADMIN;
import static com.example.trainers.AdminEndpoints.TRAINER_CREATE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE_ADMIN)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AdminController {

    CreateTrainerService createTrainerService;

    @POST
    @Path(TRAINER_CREATE)
    @Secured(roles = {ADMIN})
    public Response createTrainer(CreateTrainerRequest request) {
        return Response.status(Response.Status.CREATED)
                .entity(createTrainerService.createTrainer(request))
                .build();
    }
}