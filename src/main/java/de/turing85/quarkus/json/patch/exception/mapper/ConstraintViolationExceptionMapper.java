package de.turing85.quarkus.json.patch.exception.mapper;

import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.resteasy.reactive.server.UnwrapException;

@Provider
@Priority(Priorities.USER)
@UnwrapException(RuntimeException.class)
public class ConstraintViolationExceptionMapper
    implements ExceptionMapper<ConstraintViolationException> {
  public static final String BODY_FORMAT = "Parameter \"%s\": %s";
  public static final String UNNAMED_PROPERTY = "(unnamed)";

  @Override
  public Response toResponse(ConstraintViolationException exception) {
    // @formatter:off
    String message = exception.getConstraintViolations().stream()
        .map(ConstraintViolationExceptionMapper::constructViolationDescription)
        .collect(Collectors.joining(System.lineSeparator()));
    return Response
        .status(Status.BAD_REQUEST)
        .entity(Error.of(message))
        .build();
    // @formatter:on
  }

  private static String constructViolationDescription(ConstraintViolation<?> violation) {
    return BODY_FORMAT.formatted(getPropertyNameFromPath(violation).orElse(UNNAMED_PROPERTY),
        violation.getMessage());
  }

  private static Optional<String> getPropertyNameFromPath(ConstraintViolation<?> violation) {
    String propertyName = null;
    for (Path.Node node : violation.getPropertyPath()) {
      propertyName = node.getName();
    }
    return Optional.ofNullable(propertyName);
  }
}
