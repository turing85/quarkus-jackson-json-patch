package de.turing85.quarkus.json.patch.exception.mapper;

import java.util.Optional;

import jakarta.validation.constraints.NotNull;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(name = ErrorResponse.SCHEMA_NAME)
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RegisterForReflection
public class ErrorResponse {
  public static final String SCHEMA_NAME = "Error";

  @NotNull
  String message;

  public static ErrorResponse of(@Nullable final String message) {
    return new ErrorResponse(Optional.ofNullable(message).orElse(""));
  }
}
