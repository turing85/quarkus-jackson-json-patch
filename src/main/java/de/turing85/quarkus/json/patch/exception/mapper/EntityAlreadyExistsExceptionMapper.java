package de.turing85.quarkus.json.patch.exception.mapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import de.turing85.quarkus.json.patch.exception.EntityAlreadyExistsException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.UnwrapException;

@Provider
@Priority(Priorities.USER)
@UnwrapException(RuntimeException.class)
public final class EntityAlreadyExistsExceptionMapper
    extends BaseExceptionMapper<EntityAlreadyExistsException> {

  EntityAlreadyExistsExceptionMapper(Logger logger) {
    super(logger);
  }

  @Override
  protected int status() {
    return Response.Status.BAD_REQUEST.getStatusCode();
  }
}
