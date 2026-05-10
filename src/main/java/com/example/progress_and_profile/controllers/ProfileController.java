package com.example.progress_and_profile.controllers;

import com.example.progress_and_profile.dto.requests.UpdateProfileRequest;
import com.example.progress_and_profile.dto.responses.ProfileResponse;
import com.example.progress_and_profile.dto.responses.UpdateProfileResponse;
import com.example.progress_and_profile.services.GetUserProfileService;
import com.example.progress_and_profile.services.UpdateUserProfileService;
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

import static com.example.constants.BaseEndpoints.BASE_PROFILE;
import static com.example.constants.HttpStatuses.OK;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE_PROFILE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class ProfileController {

    GetUserProfileService getUserProfileService;
    UpdateUserProfileService updateUserProfileService;

    @GET
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Получение профиля пользователя")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = ProfileResponse.class))
    )
    public Response getUserProfile() {
        return Response.ok(getUserProfileService.getUserProfile()).build();
    }

    @PUT
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Обновление профиля пользователя")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = UpdateProfileResponse.class))
    )
    public Response updateUserProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return Response.ok(updateUserProfileService.updateUserProfile(request)).build();
    }

}
