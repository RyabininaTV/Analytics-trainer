package com.example.security.current_user_context;

import com.example.auth.exceptions.CurrentUserIsNotSetException;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.experimental.FieldDefaults;

import java.util.Optional;

import static lombok.AccessLevel.PRIVATE;

@ApplicationScoped
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class CurrentUserContext {

    ThreadLocal<CurrentUser> currentUser = new ThreadLocal<>();

    public void set(CurrentUser user) {
        currentUser.set(user);
    }

    public Optional<CurrentUser> get() {
        return Optional.ofNullable(currentUser.get());
    }

    public CurrentUser require() {
        return get().orElseThrow(CurrentUserIsNotSetException::new);
    }

    public void clear() {
        currentUser.remove();
    }

}
