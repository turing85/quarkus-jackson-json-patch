package de.turing85.quarkus.json.patch.exception.mapper;

import java.util.NoSuchElementException;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

@Provider
@Priority(Priorities.USER + 100)
public final class ThrowableExceptionMapper extends BaseExceptionMapper<NoSuchElementException> {
  ThrowableExceptionMapper(final UriInfo uriInfo, final Logger logger) {
    super(uriInfo, logger);
  }

  @Override
  protected int status() {
    return Response.Status.INTERNAL_SERVER_ERROR.getStatusCode();
  }

  @Override
  protected Logger.Level level() {
    return Logger.Level.ERROR;
  }
}
