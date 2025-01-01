package de.turing85.quarkus.json.patch.exception.mapper;

import java.util.NoSuchElementException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.reactive.server.UnwrapException;

@Provider
@Priority(Priorities.USER)
@UnwrapException(RuntimeException.class)
public final class NoSuchElementExceptionMapper
    extends BaseExceptionMapper<NoSuchElementException> {
  protected int status() {
    return Response.Status.NOT_FOUND.getStatusCode();
  }
}
