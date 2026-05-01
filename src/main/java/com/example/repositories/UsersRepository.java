package com.example.repositories;

import com.example.auth.entities.requests.CreateUserRequestEntity;
import com.example.auth.entities.responses.CreateUserResponseEntity;
import com.example.auth.entities.responses.FindUserByEmailResponseEntity;
import com.example.auth.entities.responses.FindUserByIdResponseEntity;
import com.example.auth.exceptions.UserWasNotCreatedException;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;
import java.util.Optional;

import static com.example.jooq.generated.tables.Users.USERS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UsersRepository {

    DSLContext dsl;

    public boolean existsByEmail(String email) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(USERS)
                        .where(USERS.EMAIL.eq(email))
        );
    }

    public boolean existsByUsername(String username) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(USERS)
                        .where(USERS.USERNAME.eq(username))
        );
    }

    public CreateUserResponseEntity create(@Nonnull CreateUserRequestEntity request) {
        return dsl.insertInto(USERS)
                .set(USERS.EMAIL, request.email())
                .set(USERS.USERNAME, request.username())
                .set(USERS.PASSWORD_HASH, request.passwordHash())
                .returning(
                        USERS.ID,
                        USERS.EMAIL,
                        USERS.USERNAME,
                        USERS.ROLE,
                        USERS.STATUS,
                        USERS.CREATED_AT,
                        USERS.UPDATED_AT
                )
                .fetchOptional()
                .map(record -> CreateUserResponseEntity.builder()
                        .id(record.get(USERS.ID))
                        .email(record.get(USERS.EMAIL))
                        .username(record.get(USERS.USERNAME))
                        .role(record.get(USERS.ROLE))
                        .status(record.get(USERS.STATUS))
                        .createdAt(record.get(USERS.CREATED_AT))
                        .updatedAt(record.get(USERS.UPDATED_AT))
                        .build()
                )
                .orElseThrow(UserWasNotCreatedException::new);
    }

    public Optional<FindUserByEmailResponseEntity> findByEmail(String email) {
        return dsl.select(
                        USERS.ID,
                        USERS.EMAIL,
                        USERS.USERNAME,
                        USERS.PASSWORD_HASH,
                        USERS.ROLE,
                        USERS.STATUS,
                        USERS.CREATED_AT,
                        USERS.UPDATED_AT
                )
                .from(USERS)
                .where(USERS.EMAIL.eq(email))
                .fetchOptional(record -> FindUserByEmailResponseEntity.builder()
                        .id(record.get(USERS.ID))
                        .email(record.get(USERS.EMAIL))
                        .username(record.get(USERS.USERNAME))
                        .passwordHash(record.get(USERS.PASSWORD_HASH))
                        .role(record.get(USERS.ROLE))
                        .status(record.get(USERS.STATUS))
                        .createdAt(record.get(USERS.CREATED_AT))
                        .updatedAt(record.get(USERS.UPDATED_AT))
                        .build()
                );
    }

    public Optional<FindUserByIdResponseEntity> findById(@Nonnull Long id) {
        return dsl.select(
                        USERS.ID,
                        USERS.EMAIL,
                        USERS.USERNAME,
                        USERS.ROLE,
                        USERS.STATUS,
                        USERS.CREATED_AT,
                        USERS.UPDATED_AT
                )
                .from(USERS)
                .where(USERS.ID.eq(id))
                .fetchOptional(record -> FindUserByIdResponseEntity.builder()
                        .id(record.get(USERS.ID))
                        .email(record.get(USERS.EMAIL))
                        .username(record.get(USERS.USERNAME))
                        .role(record.get(USERS.ROLE))
                        .status(record.get(USERS.STATUS))
                        .createdAt(record.get(USERS.CREATED_AT))
                        .updatedAt(record.get(USERS.UPDATED_AT))
                        .build()
                );
    }

}
