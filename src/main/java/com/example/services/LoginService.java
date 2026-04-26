package com.example.services;

import com.example.dto.requests.LoginRequest;
import com.example.dto.responses.AuthResponse;
import com.example.entities.responses.FindUserByEmailResponseEntity;
import com.example.exceptions.InvalidEmailOrPasswordException;
import com.example.exceptions.UserIsBlockedException;
import com.example.repositories.UserRepository;
import com.example.utils.Checker;
import com.example.utils.EmailUtil;
import com.example.utils.JwtUtil;
import com.example.yaml.AppYamlConfig;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import static com.example.jooq.generated.enums.UserStatusEnum.ACTIVE;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class LoginService {

    UserRepository userRepository;

    AppYamlConfig appYaml;

    public AuthResponse login(@Nonnull LoginRequest request) {
        String email = EmailUtil.normalize(request.email());

        FindUserByEmailResponseEntity user = userRepository.findByEmail(email)
                .orElseThrow(InvalidEmailOrPasswordException::new);

        Checker.begin()
                .when(!BcryptUtil.matches(request.password(), user.passwordHash()))
                .thenThrow(InvalidEmailOrPasswordException::new)
                .when(user.status() != ACTIVE)
                .thenThrow(UserIsBlockedException::new);

        JwtUtil.JwtUserClaims jwtUserClaims = mapToJwtUserClaims(user);

        return AuthResponse.builder()
                .accessToken(JwtUtil.createAccessToken(jwtUserClaims, appYaml))
                .refreshToken(JwtUtil.createRefreshToken(jwtUserClaims, appYaml))
                .user(mapToUserResponse(user))
                .build();
    }

    private static JwtUtil.JwtUserClaims mapToJwtUserClaims(@Nonnull FindUserByEmailResponseEntity user) {
        return JwtUtil.JwtUserClaims.builder()
                .id(user.id())
                .email(user.email())
                .username(user.username())
                .role(user.role())
                .build();
    }

    private static AuthResponse.UserResponse mapToUserResponse(@Nonnull FindUserByEmailResponseEntity user) {
        return AuthResponse.UserResponse.builder()
                .id(user.id())
                .email(user.email())
                .username(user.username())
                .role(user.role())
                .status(user.status())
                .build();
    }

}
