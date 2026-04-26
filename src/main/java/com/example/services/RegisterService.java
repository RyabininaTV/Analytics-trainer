package com.example.services;

import com.example.dto.requests.RegisterRequest;
import com.example.dto.responses.AuthResponse;
import com.example.entities.requests.CreateUserRequestEntity;
import com.example.entities.responses.CreateUserResponseEntity;
import com.example.exceptions.EmailIsAlreadyUsedException;
import com.example.exceptions.UsernameIsAlreadyUsedException;
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

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RegisterService {

    UserRepository userRepository;

    AppYamlConfig appYaml;

    public AuthResponse register(@Nonnull RegisterRequest request) {
        String email = EmailUtil.normalize(request.email());
        String username = request.username().trim();

        Checker.begin()
                .when(userRepository.existsByEmail(email))
                .thenThrow(EmailIsAlreadyUsedException::new)
                .when(userRepository.existsByUsername(username))
                .thenThrow(UsernameIsAlreadyUsedException::new);

        String passwordHash = BcryptUtil.bcryptHash(request.password());

        CreateUserResponseEntity user = userRepository.create(CreateUserRequestEntity.builder()
                        .email(email)
                        .username(username)
                        .passwordHash(passwordHash)
                        .build()
        );

        JwtUtil.JwtUserClaims jwtUserClaims = mapToJwtUserClaims(user);

        return AuthResponse.builder()
                .accessToken(JwtUtil.createAccessToken(jwtUserClaims, appYaml))
                .refreshToken(JwtUtil.createRefreshToken(jwtUserClaims, appYaml))
                .user(mapToUserResponse(user))
                .build();
    }

    private static JwtUtil.JwtUserClaims mapToJwtUserClaims(@Nonnull CreateUserResponseEntity user) {
        return JwtUtil.JwtUserClaims.builder()
                .id(user.id())
                .email(user.email())
                .username(user.username())
                .role(user.role())
                .build();
    }

    private static AuthResponse.UserResponse mapToUserResponse(@Nonnull CreateUserResponseEntity user) {
        return AuthResponse.UserResponse.builder()
                .id(user.id())
                .email(user.email())
                .username(user.username())
                .role(user.role())
                .status(user.status())
                .build();
    }

}
