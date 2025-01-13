package de.turing85.quarkus.json.patch.exception.mapper;

import java.net.URI;
import java.util.Optional;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import jakarta.ws.rs.ext.ExceptionMapper;

import io.quarkiverse.resteasy.problem.ExceptionMapperBase;
import io.quarkiverse.resteasy.problem.HttpProblem;
import io.quarkiverse.resteasy.problem.postprocessing.ProblemContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jboss.logging.Logger;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseExceptionMapper<T extends Throwable> implements ExceptionMapper<T> {
  private final UriInfo uriInfo;
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
    final HttpProblem problem = HttpProblem.builder()
            .withStatus(status)
            .withTitle(Optional.ofNullable(Response.Status.fromStatusCode(status))
                .map(Response.Status::getReasonPhrase)
                .orElse("<unknown>"))
            .withDetail(throwable.getMessage())
            .withInstance(URI.create(uriInfo.getPath()))
            .build();
    return ExceptionMapperBase.postProcessorsRegistry
        .applyPostProcessing(
            problem,
            ProblemContext.of(throwable, uriInfo))
        .toResponse();
    // @formatter:on
  }
}
