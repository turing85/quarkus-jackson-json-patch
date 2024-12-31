package de.turing85.quarkus.json.patch.exception.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import io.quarkus.logging.Log;

public abstract class BaseExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {
  protected abstract int status();

  @Override
  public final Response toResponse(T throwable) {
    Log.infof(throwable, "Caught exception, responding with %s", status());
    // @formatter:off
    return Response
        .status(status())
        .entity(ErrorDto.builder().message(throwable.getMessage()).build())
        .build();
    // @formatter:on
  }
}
