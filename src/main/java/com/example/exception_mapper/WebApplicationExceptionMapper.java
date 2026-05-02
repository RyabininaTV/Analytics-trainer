package com.example.exception_mapper;

import com.example.exception_mapper.dto.responses.ErrorResponse;
import jakarta.annotation.Nonnull;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Provider
public class WebApplicationExceptionMapper implements ExceptionMapper<WebApplicationException> {

    private static final Logger LOG = Logger.getLogger(WebApplicationExceptionMapper.class);

    @Override
    public Response toResponse(@Nonnull WebApplicationException exception) {
        Response originalResponse = exception.getResponse();

        int status = originalResponse != null
                ? originalResponse.getStatus()
                : Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();

        if (status >= 500) {
            LOG.error("Server error", exception);
        }

        return Response.status(status)
                .entity(ErrorResponse.builder()
                        .code(resolveCode(exception))
                        .message(exception.getMessage())
                        .build()
                )
                .build();
    }

    @Nonnull
    private String resolveCode(@Nonnull WebApplicationException exception) {
        return exception.getClass()
                .getSimpleName()
                .replace("Exception", "")
                .replaceAll("([a-z])([A-Z])", "$1_$2")
                .toUpperCase();
    }

}
