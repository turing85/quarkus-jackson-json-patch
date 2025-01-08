package de.turing85.quarkus.json.patch.exception.mapper;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {
  private final Logger logger;

  protected abstract int statusFor(T throwable);

  protected Logger.Level level() {
    return Logger.Level.INFO;
  }

  protected final Logger logger() {
    return logger;
  }

  @Override
  public final Response toResponse(final T throwable) {
    final int status = statusFor(throwable);
    logger().logf(level(), throwable, "Caught exception, responding with status %s", status);
    // @formatter:off
    return Response
        .status(status)
        .entity(Error.of(throwable.getMessage()))
        .build();
    // @formatter:on
  }
}
