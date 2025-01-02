package de.turing85.quarkus.json.patch.exception.mapper;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RegisterForReflection
public class Error {
  String message;

  public static Error of(String message) {
    return new Error(message);
  }
}
