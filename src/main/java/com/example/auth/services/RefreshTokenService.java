package com.example.auth.services;

import com.example.auth.dto.requests.RefreshRequest;
import com.example.auth.dto.responses.AuthResponse;
import com.example.auth.entities.responses.FindUserByIdResponseEntity;
import com.example.auth.exceptions.InvalidRefreshTokenException;
import com.example.auth.exceptions.RefreshTokenIsRevokedException;
import com.example.auth.exceptions.UserIsBlockedException;
import com.example.repositories.RevokedTokensRepository;
import com.example.repositories.UsersRepository;
import com.example.utils.JwtUtil;
import com.example.yaml.AppYamlConfig;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static com.example.jooq.generated.enums.UserStatusEnum.ACTIVE;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RefreshTokenService {

    UsersRepository usersRepository;
    RevokedTokensRepository revokedTokensRepository;

    AppYamlConfig appYaml;

    public AuthResponse refresh(@Nonnull RefreshRequest request) {
        Claims claims = JwtUtil.parseAndValidate(request.refreshToken(), appYaml);

        if (!JwtUtil.isRefreshToken(claims)) {
            throw new InvalidRefreshTokenException();
        }
        if (revokedTokensRepository.existsActiveByTokenId(claims.getId())) {
            throw new RefreshTokenIsRevokedException();
        }

        FindUserByIdResponseEntity user = usersRepository.findById(Long.valueOf(claims.getSubject()))
                .orElseThrow(InvalidRefreshTokenException::new);

        if (user.status() != ACTIVE) {
            throw new UserIsBlockedException();
        }

        JwtUtil.JwtUserClaims jwtUserClaims = mapToJwtUserClaims(user);

        return AuthResponse.builder()
                .accessToken(JwtUtil.createAccessToken(jwtUserClaims, appYaml))
                .refreshToken(JwtUtil.createRefreshToken(jwtUserClaims, appYaml))
                .user(mapToUserResponse(user))
                .build();
    }

    private static JwtUtil.JwtUserClaims mapToJwtUserClaims(@Nonnull FindUserByIdResponseEntity user) {
        return JwtUtil.JwtUserClaims.builder()
                .id(user.id())
                .email(user.email())
                .username(user.username())
                .role(user.role())
                .build();
    }

    private static AuthResponse.UserResponse mapToUserResponse(@Nonnull FindUserByIdResponseEntity user) {
        return AuthResponse.UserResponse.builder()
                .id(user.id())
                .email(user.email())
                .username(user.username())
                .role(user.role())
                .status(user.status())
                .build();
    }

}
