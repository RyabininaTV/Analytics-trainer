package com.example.yaml;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithName;

@StaticInitSafe
@ConfigMapping(prefix = "app")
public interface AppYamlConfig {

    @WithName("jwt")
    Jwt jwt();

    interface Jwt {

        @WithName("secret")
        String secret();

        @WithName("issuer")
        String issuer();

        @WithName("access-token-ttl-minutes")
        long accessTokenTtlMinutes();

        @WithName("refresh-token-ttl-days")
        long refreshTokenTtlDays();

    }

}
