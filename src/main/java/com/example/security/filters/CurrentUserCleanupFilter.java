package com.example.security.filters;

import com.example.security.current_user_context.CurrentUserContext;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class CurrentUserCleanupFilter implements ContainerResponseFilter {

    @Inject
    CurrentUserContext currentUserContext;

    @Override
    public void filter(
            ContainerRequestContext requestContext,
            ContainerResponseContext responseContext
    ) {
        currentUserContext.clear();
    }

}
