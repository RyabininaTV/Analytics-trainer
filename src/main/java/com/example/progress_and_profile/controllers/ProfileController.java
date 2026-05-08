package com.example.progress_and_profile.controllers;

import com.example.progress_and_profile.dto.requests.UpdateProfileRequest;
import com.example.progress_and_profile.services.GetUserProfileService;
import com.example.progress_and_profile.services.UpdateUserProfileService;
import com.example.security.annotations.Secured;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;

import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static com.example.progress_and_profile.endpoints.ProfileEndpoints.BASE;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE)
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
    public Response getUserProfile() {
        return Response.ok(getUserProfileService.getUserProfile()).build();
    }

    @PUT
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Обновление профиля пользователя")
    public Response updateUserProfile(@Valid @RequestBody UpdateProfileRequest request) {
        return Response.ok(updateUserProfileService.updateUserProfile(request)).build();
    }

}
