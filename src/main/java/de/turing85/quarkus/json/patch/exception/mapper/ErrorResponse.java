package de.turing85.quarkus.json.patch.exception.mapper;

import java.util.Optional;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.jspecify.annotations.Nullable;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RegisterForReflection
public class ErrorResponse {
  String message;

  public static ErrorResponse of(@Nullable final String message) {
    return new ErrorResponse(Optional.ofNullable(message).orElse(""));
  }
}
