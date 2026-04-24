package com.example.repositories;

import com.example.entities.User;
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
public class UserRepository {

    DSLContext dsl;

    public Optional<User> findById(Long id) {
        return dsl.selectFrom(USERS)
                .where(USERS.ID.eq(id))
                .fetchOptional(record -> User.builder()
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
