package de.turing85.quarkus.json.patch.exception.mapper;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@RegisterForReflection
public final class Error {
  private final String message;
}
