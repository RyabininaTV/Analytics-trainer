package com.example.repositories;

import com.example.auth.entities.requests.CreateRevokedTokenRequestEntity;
import jakarta.annotation.Nonnull;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jooq.DSLContext;

import java.time.LocalDateTime;

import static com.example.jooq.generated.tables.RevokedTokens.REVOKED_TOKENS;
import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RevokedTokensRepository {

    DSLContext dsl;

    public void create(@Nonnull CreateRevokedTokenRequestEntity request) {
        dsl.insertInto(REVOKED_TOKENS)
                .set(REVOKED_TOKENS.TOKEN_ID, request.tokenId())
                .set(REVOKED_TOKENS.EXPIRES_AT, request.expiresAt())
                .onConflict(REVOKED_TOKENS.TOKEN_ID)
                .doNothing()
                .execute();
    }

    public boolean existsActiveByTokenId(@Nonnull String tokenId) {
        return dsl.fetchExists(
                dsl.selectOne()
                        .from(REVOKED_TOKENS)
                        .where(REVOKED_TOKENS.TOKEN_ID.eq(tokenId))
                        .and(REVOKED_TOKENS.EXPIRES_AT.gt(LocalDateTime.now()))
        );
    }

}
