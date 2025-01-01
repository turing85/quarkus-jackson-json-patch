package de.turing85.quarkus.json.patch.exception.mapper;

import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import com.github.fge.jsonpatch.JsonPatchException;
import org.jboss.resteasy.reactive.server.UnwrapException;

@Provider
@Priority(Priorities.USER)
@UnwrapException(RuntimeException.class)
public final class JsonPatchExceptionMapper extends BaseExceptionMapper<JsonPatchException> {
  protected int status() {
    return Response.Status.BAD_REQUEST.getStatusCode();
  }
}
