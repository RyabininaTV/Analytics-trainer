package com.example.ratings_and_achievements;

import com.example.ratings_and_achievements.service.GetLeaderboardByTrainerService;
import com.example.ratings_and_achievements.service.GetLeaderboardService;
import com.example.security.annotations.Secured;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;

import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.ratings_and_achievements.LeatherboardEndpoints.BASE;
import static com.example.ratings_and_achievements.LeatherboardEndpoints.BY_TRAINER_ID;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LeaderboardController {

    GetLeaderboardService getLeaderboardService;
    GetLeaderboardByTrainerService getLeaderboardByTrainerService;

    @GET
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Глобальный рейтинг всех пользователей")
    public Response getLeaderboard() {
        return Response.ok(getLeaderboardService.getLeaderboard()).build();
    }

    @GET
    @Path(BY_TRAINER_ID)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Рейтинг по конкретному тренажеру")
    public Response getLeaderboardByTrainer(@PathParam("trainerId") long trainerId) {
        return Response.ok(getLeaderboardByTrainerService.getLeaderboardByTrainer(trainerId)).build();
    }

}
