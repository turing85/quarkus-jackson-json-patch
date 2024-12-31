package de.turing85.quarkus.json.patch.exception.mapper;

import java.util.NoSuchElementException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER + 100)
public final class ThrowableExceptionMapper extends BaseExceptionMapper<NoSuchElementException> {
  protected int status() {
    return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
  }
}
