package de.turing85.quarkus.json.patch.exception.mapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.UnwrapException;

@Provider
@Priority(Priorities.USER + 50)
@UnwrapException(RuntimeException.class)
public final class JsonPatchApplicationExceptionMapper
    extends BaseExceptionMapper<JsonPatchApplicationException> {
  JsonPatchApplicationExceptionMapper(final UriInfo uriInfo, final Logger logger) {
    super(uriInfo, logger);
  }

  @Override
  protected int status() {
    return Response.Status.BAD_REQUEST.getStatusCode();
  }
}
