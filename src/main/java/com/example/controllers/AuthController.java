package com.example.controllers;

import com.example.dto.requests.*;
import com.example.security.annotations.Secured;
import com.example.services.*;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.openapi.annotations.Operation;

import static com.example.constants.AuthEndpoints.*;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CREATED;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthController {

    RegisterService registerService;
    LoginService loginService;
    LogoutService logoutService;
    RefreshTokenService refreshTokenService;

    @POST
    @Path(REGISTER)
    @Operation(summary = "Регистрация пользователя")
    public Response register(@Valid RegisterRequest request) {
        return Response.status(CREATED)
                .entity(registerService.register(request))
                .build();
    }

    @POST
    @Path(LOGIN)
    @Operation(summary = "Вход пользователя")
    public Response login(@Valid LoginRequest request) {
        return Response.ok(loginService.login(request)).build();
    }

    @POST
    @Path(LOGOUT)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Выход пользователя")
    public Response logout(
            @Valid LogoutRequest request,
            @Context HttpHeaders headers
    ) {
        logoutService.logout(request, headers);
        return Response.noContent().build();
    }

    @POST
    @Path(REFRESH)
    @Operation(summary = "Обновление токенов")
    public Response refresh(@Valid RefreshRequest request) {
        return Response.ok(refreshTokenService.refresh(request)).build();
    }

}
