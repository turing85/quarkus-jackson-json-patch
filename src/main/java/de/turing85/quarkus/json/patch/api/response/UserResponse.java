package de.turing85.quarkus.json.patch.api.response;

import java.util.Objects;

import de.turing85.quarkus.json.patch.spi.User;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public record UserResponse(String name, String email) implements User {
  public static UserResponse from(final User user) {
    return new UserResponse(user.name(), user.email());
  }

  @Override
  public boolean equals(final Object that) {
    if (this == that) {
      return true;
    }
    return that instanceof UserResponse(final String thatName, final String thatEmail)
        && Objects.equals(name, thatName)
        && Objects.equals(email, thatEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, email);
  }
}
