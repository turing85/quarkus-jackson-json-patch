package de.turing85.quarkus.json.patch.exception.mapper;

import java.util.Optional;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import com.flipkart.zjsonpatch.JsonPatchApplicationException;
import com.flipkart.zjsonpatch.Operation;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.UnwrapException;

@Provider
@Priority(Priorities.USER)
@UnwrapException(RuntimeException.class)
public final class JsonPatchApplicationExceptionMapper
    extends BaseExceptionMapper<JsonPatchApplicationException> {
  JsonPatchApplicationExceptionMapper(final UriInfo uriInfo, final Logger logger) {
    super(uriInfo, logger);
  }

  @Override
  protected int statusFor(JsonPatchApplicationException exception) {
    if (exception.getOperation().equals(Operation.TEST)
        && Optional.ofNullable(exception.getMessage()).orElse("").startsWith("Expected")) {
      return Response.Status.CONFLICT.getStatusCode();
    }
    return Response.Status.BAD_REQUEST.getStatusCode();
  }
}
