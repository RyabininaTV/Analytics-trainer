package com.example.security.filters;

import com.example.exceptions.InvalidJwtException;
import com.example.jooq.generated.enums.UserRoleEnum;
import com.example.repositories.RevokedTokenRepository;
import com.example.security.annotations.Secured;
import com.example.security.current_user_context.CurrentUser;
import com.example.security.current_user_context.CurrentUserContext;
import com.example.utils.JwtUtil;
import com.example.yaml.AppYamlConfig;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Priority;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Arrays;

import static jakarta.ws.rs.Priorities.AUTHENTICATION;
import static lombok.AccessLevel.PRIVATE;

@Secured
@Provider
@RequiredArgsConstructor
@Priority(AUTHENTICATION)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class JwtAuthFilter implements ContainerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String ACCESS_TOKEN_TYPE = "access";

    private static final String EMAIL_CLAIM = "email";
    private static final String USERNAME_CLAIM = "username";
    private static final String ROLE_CLAIM = "role";

    RevokedTokenRepository revokedTokenRepository;

    CurrentUserContext currentUserContext;

    AppYamlConfig appYaml;

    @Context
    ResourceInfo resourceInfo;

    @Override
    public void filter(@Nonnull ContainerRequestContext requestContext) {
        String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Authorization header is required");
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        Claims claims;

        try {
            claims = JwtUtil.parseAndValidate(token, appYaml);
        } catch (InvalidJwtException e) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Invalid token");
            return;
        }

        String tokenType = claims.get(TOKEN_TYPE_CLAIM, String.class);

        if (!ACCESS_TOKEN_TYPE.equals(tokenType)) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Only access token is allowed");
            return;
        }

        if (revokedTokenRepository.existsActiveByTokenId(claims.getId())) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Token has been revoked");
            return;
        }

        UserRoleEnum role;

        try {
            role = UserRoleEnum.valueOf(claims.get(ROLE_CLAIM, String.class));
        } catch (IllegalArgumentException | NullPointerException e) {
            abort(requestContext, Response.Status.UNAUTHORIZED, "Invalid user role");
            return;
        }

        CurrentUser currentUser = CurrentUser.builder()
                .id(Long.valueOf(claims.getSubject()))
                .email(claims.get(EMAIL_CLAIM, String.class))
                .username(claims.get(USERNAME_CLAIM, String.class))
                .role(role)
                .build();

        currentUserContext.set(currentUser);

        Secured secured = getSecuredAnnotation();

        if (secured != null && secured.roles().length > 0) {
            boolean hasRequiredRole = Arrays.asList(secured.roles()).contains(currentUser.role());

            if (!hasRequiredRole) {
                abort(requestContext, Response.Status.FORBIDDEN, "Access denied");
            }
        }
    }

    private Secured getSecuredAnnotation() {
        Secured methodAnnotation = resourceInfo.getResourceMethod().getAnnotation(Secured.class);

        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        return resourceInfo.getResourceClass().getAnnotation(Secured.class);
    }

    private static void abort(
            @Nonnull ContainerRequestContext requestContext,
            @Nonnull Response.Status status,
            @Nonnull String message
    ) {
        requestContext.abortWith(
                Response.status(status)
                        .entity(ErrorResponse.builder()
                                .message(message)
                                .build()
                        )
                        .build()
        );
    }

    @Builder
    private record ErrorResponse(

            String message

    ) {}

}
