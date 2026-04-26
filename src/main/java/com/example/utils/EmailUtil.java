package com.example.utils;

import jakarta.annotation.Nonnull;
import lombok.experimental.UtilityClass;

import static java.util.Locale.ROOT;

@UtilityClass
public class EmailUtil {

    @Nonnull
    public static String normalize(@Nonnull String email) {
        return email.trim().toLowerCase(ROOT);
    }

}
