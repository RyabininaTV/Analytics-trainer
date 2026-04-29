package com.example.auth.services;

import com.example.auth.dto.requests.RegisterRequest;
import com.example.auth.dto.responses.AuthResponse;
import com.example.auth.entities.requests.CreateUserRequestEntity;
import com.example.auth.entities.responses.CreateUserResponseEntity;
import com.example.auth.exceptions.EmailIsAlreadyUsedException;
import com.example.auth.exceptions.UsernameIsAlreadyUsedException;
import com.example.repositories.UsersRepository;
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

    UsersRepository usersRepository;

    AppYamlConfig appYaml;

    public AuthResponse register(@Nonnull RegisterRequest request) {
        String email = EmailUtil.normalize(request.email());
        String username = request.username().trim();

        if (usersRepository.existsByEmail(email)) {
            throw new EmailIsAlreadyUsedException();
        }
        if (usersRepository.existsByUsername(username)) {
            throw new UsernameIsAlreadyUsedException();
        }

        String passwordHash = BcryptUtil.bcryptHash(request.password());

        CreateUserResponseEntity user = usersRepository.create(CreateUserRequestEntity.builder()
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
