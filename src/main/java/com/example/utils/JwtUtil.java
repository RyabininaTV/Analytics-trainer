package com.example.utils;

import com.example.auth.exceptions.InvalidJwtException;
import com.example.jooq.generated.enums.UserRoleEnum;
import com.example.yaml.AppYamlConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Nonnull;
import lombok.Builder;
import lombok.experimental.UtilityClass;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@UtilityClass
public class JwtUtil {

    private static final String ACCESS_TOKEN_TYPE = "access";
    private static final String REFRESH_TOKEN_TYPE = "refresh";

    private static final String TOKEN_TYPE_CLAIM = "token_type";
    private static final String EMAIL_CLAIM = "email";
    private static final String USERNAME_CLAIM = "username";
    private static final String ROLE_CLAIM = "role";

    private static final int ACCESS_TOKEN_MINUTES_TO_SECONDS_MULTIPLIER = 60;
    private static final int REFRESH_TOKEN_DAYS_TO_SECONDS_MULTIPLIER = 24 * 60 * 60;

    public static String createAccessToken(
            @Nonnull JwtUserClaims user,
            @Nonnull AppYamlConfig appYaml
    ) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(
                appYaml.jwt().accessTokenTtlMinutes()
                        * ACCESS_TOKEN_MINUTES_TO_SECONDS_MULTIPLIER
        );

        return createToken(user, ACCESS_TOKEN_TYPE, now, expiresAt, appYaml);
    }

    public static String createRefreshToken(
            @Nonnull JwtUserClaims user,
            @Nonnull AppYamlConfig appYaml
    ) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(
                appYaml.jwt().refreshTokenTtlDays()
                        * REFRESH_TOKEN_DAYS_TO_SECONDS_MULTIPLIER
        );

        return createToken(user, REFRESH_TOKEN_TYPE, now, expiresAt, appYaml);
    }

    public static Claims parseAndValidate(
            @Nonnull String token,
            @Nonnull AppYamlConfig appYaml
    ) {
        try {
            Jws<Claims> parsedToken = Jwts.parser()
                    .verifyWith(createSecretKey(appYaml.jwt().secret()))
                    .requireIssuer(appYaml.jwt().issuer())
                    .build()
                    .parseSignedClaims(token);

            return parsedToken.getPayload();
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtException("Invalid JWT token");
        }
    }

    private static String createToken(
            @Nonnull JwtUserClaims user,
            @Nonnull String tokenType,
            @Nonnull Instant issuedAt,
            @Nonnull Instant expiresAt,
            @Nonnull AppYamlConfig appYaml
    ) {
        return Jwts.builder()
                .issuer(appYaml.jwt().issuer())
                .subject(String.valueOf(user.id()))
                .id(UUID.randomUUID().toString())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiresAt))
                .claim(TOKEN_TYPE_CLAIM, tokenType)
                .claim(EMAIL_CLAIM, user.email())
                .claim(USERNAME_CLAIM, user.username())
                .claim(ROLE_CLAIM, user.role().name())
                .signWith(createSecretKey(appYaml.jwt().secret()))
                .compact();
    }

    @Nonnull
    private static SecretKey createSecretKey(@Nonnull String jwtSecret) {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public static boolean isRefreshToken(@Nonnull Claims claims) {
        return REFRESH_TOKEN_TYPE.equals(claims.get(TOKEN_TYPE_CLAIM, String.class));
    }

    @Builder
    public record JwtUserClaims(

            @Nonnull
            Long id,

            @Nonnull
            String email,

            @Nonnull
            String username,

            @Nonnull
            UserRoleEnum role

    ) {}

}
