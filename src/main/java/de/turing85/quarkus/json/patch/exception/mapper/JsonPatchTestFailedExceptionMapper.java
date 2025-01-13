package de.turing85.quarkus.json.patch.exception.mapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.Provider;

import com.flipkart.zjsonpatch.JsonPatchTestFailedException;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.server.UnwrapException;

@Provider
@Priority(Priorities.USER)
@UnwrapException(RuntimeException.class)
public final class JsonPatchTestFailedExceptionMapper
    extends BaseExceptionMapper<JsonPatchTestFailedException> {
  JsonPatchTestFailedExceptionMapper(UriInfo uriInfo, final Logger logger) {
    super(uriInfo, logger);
  }

  @Override
  protected int status() {
    return Response.Status.CONFLICT.getStatusCode();
  }
}
