package de.turing85.quarkus.json.patch.api.response;

import de.turing85.quarkus.json.patch.spi.User;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserResponse(String name, String email) implements User {
  public static UserResponse of(final User user) {
    return new UserResponse(user.name(), user.email());
  }

  public static UserResponse of(final String name, final String email) {
    return new UserResponse(name, email);
  }
}
