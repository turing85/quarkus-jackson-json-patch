package de.turing85.quarkus.json.patch.exception.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

@RequiredArgsConstructor
public abstract class BaseExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {
  private final Logger logger;

  protected abstract int status();

  protected Logger.Level level() {
    return Logger.Level.INFO;
  }

  protected final Logger logger() {
    return logger;
  }

  @Override
  public final Response toResponse(T throwable) {
    // @formatter:off
    logger().logf(level(), throwable, "Caught exception, responding with status %s", status());
    return Response
        .status(status())
        .entity(Error.builder().message(throwable.getMessage()).build())
        .build();
    // @formatter:on
  }
}
