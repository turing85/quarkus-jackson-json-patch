package de.turing85.quarkus.json.patch.api.request;

import java.util.Objects;

import de.turing85.quarkus.json.patch.spi.User;

public record CreateUserRequest(String name, String email) implements User {
  public static CreateUserRequest from(final User user) {
    return new CreateUserRequest(user.name(), user.email());
  }

  @Override
  public boolean equals(final Object that) {
    if (this == that) {
      return true;
    }
    return that instanceof CreateUserRequest(final String thatName, final String thatEmail)
        && Objects.equals(name, thatName)
        && Objects.equals(email, thatEmail);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, email);
  }
}
