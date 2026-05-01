package com.example.auth.services;

import com.example.auth.dto.requests.LoginRequest;
import com.example.auth.dto.responses.AuthResponse;
import com.example.auth.entities.responses.FindUserByEmailResponseEntity;
import com.example.auth.exceptions.InvalidEmailOrPasswordException;
import com.example.auth.exceptions.UserIsBlockedException;
import com.example.repositories.UsersRepository;
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

    UsersRepository usersRepository;

    AppYamlConfig appYaml;

    public AuthResponse login(@Nonnull LoginRequest request) {
        String email = EmailUtil.normalize(request.email());

        FindUserByEmailResponseEntity user = usersRepository.findByEmail(email)
                .orElseThrow(InvalidEmailOrPasswordException::new);

        if (!BcryptUtil.matches(request.password(), user.passwordHash())) {
            throw new InvalidEmailOrPasswordException();
        }
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
