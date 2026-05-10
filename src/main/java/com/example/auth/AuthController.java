package com.example.auth;

import com.example.auth.dto.requests.LoginRequest;
import com.example.auth.dto.requests.LogoutRequest;
import com.example.auth.dto.requests.RefreshRequest;
import com.example.auth.dto.requests.RegisterRequest;
import com.example.auth.dto.responses.AuthResponse;
import com.example.auth.services.LoginService;
import com.example.auth.services.LogoutService;
import com.example.auth.services.RefreshTokenService;
import com.example.auth.services.RegisterService;
import com.example.security.annotations.Secured;
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
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

import static com.example.constants.BaseEndpoints.BASE_AUTH;
import static com.example.constants.HttpStatuses.*;
import static com.example.jooq.generated.enums.UserRoleEnum.ADMIN;
import static com.example.jooq.generated.enums.UserRoleEnum.USER;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static lombok.AccessLevel.PRIVATE;

@Path(BASE_AUTH)
@RequiredArgsConstructor
@Consumes(APPLICATION_JSON)
@Produces(APPLICATION_JSON)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class AuthController {

    public static final String REGISTER = "/register";
    public static final String LOGIN = "/login";
    public static final String LOGOUT = "/logout";
    public static final String REFRESH = "/refresh";

    RegisterService registerService;
    LoginService loginService;
    LogoutService logoutService;
    RefreshTokenService refreshTokenService;

    @POST
    @Path(REGISTER)
    @Operation(summary = "Регистрация пользователя")
    @APIResponse(
            responseCode = CREATED,
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
    )
    public Response register(@Valid @RequestBody RegisterRequest request) {
        return Response.status(Integer.parseInt(CREATED))
                .entity(registerService.register(request))
                .build();
    }

    @POST
    @Path(LOGIN)
    @Operation(summary = "Вход пользователя")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
    )
    public Response login(@Valid @RequestBody LoginRequest request) {
        return Response.ok(loginService.login(request)).build();
    }

    @POST
    @Path(LOGOUT)
    @Secured(roles = {USER, ADMIN})
    @Operation(summary = "Выход пользователя")
    @APIResponse(responseCode = NO_CONTENT)
    public Response logout(
            @Valid @RequestBody LogoutRequest request,
            @Context HttpHeaders headers
    ) {
        logoutService.logout(request, headers);
        return Response.noContent().build();
    }

    @POST
    @Path(REFRESH)
    @Operation(summary = "Обновление токенов")
    @APIResponse(
            responseCode = OK,
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
    )
    public Response refresh(@Valid @RequestBody RefreshRequest request) {
        return Response.ok(refreshTokenService.refresh(request)).build();
    }

}
