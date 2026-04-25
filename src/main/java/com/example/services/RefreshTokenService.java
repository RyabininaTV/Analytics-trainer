package com.example.services;

import com.example.dto.requests.RefreshRequest;
import com.example.dto.responses.AuthResponse;
import com.example.entities.responses.FindUserByIdResponseEntity;
import com.example.exceptions.InvalidRefreshTokenException;
import com.example.exceptions.RefreshTokenIsRevokedException;
import com.example.exceptions.UserIsBlockedException;
import com.example.repositories.RevokedTokenRepository;
import com.example.repositories.UserRepository;
import com.example.utils.Checker;
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

    UserRepository userRepository;
    RevokedTokenRepository revokedTokenRepository;

    AppYamlConfig appYaml;

    public AuthResponse refresh(@Nonnull RefreshRequest request) {
        Claims claims = JwtUtil.parseAndValidate(request.refreshToken(), appYaml);

        Checker.begin()
                .when(!JwtUtil.isRefreshToken(claims))
                .thenThrow(InvalidRefreshTokenException::new)
                .when(revokedTokenRepository.existsActiveByTokenId(claims.getId()))
                .thenThrow(RefreshTokenIsRevokedException::new);

        FindUserByIdResponseEntity user = userRepository.findById(Long.valueOf(claims.getSubject()))
                .orElseThrow(InvalidRefreshTokenException::new);

        Checker.begin()
                .when(user.status() != ACTIVE)
                .thenThrow(UserIsBlockedException::new);

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
