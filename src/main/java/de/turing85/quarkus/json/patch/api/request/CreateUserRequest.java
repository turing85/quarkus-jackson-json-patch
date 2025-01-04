package de.turing85.quarkus.json.patch.api.request;

import de.turing85.quarkus.json.patch.spi.User;

public record CreateUserRequest(String name, String email) implements User {
  public static CreateUserRequest of(final User user) {
    return new CreateUserRequest(user.name(), user.email());
  }

  public static CreateUserRequest of(final String name, final String email) {
    return new CreateUserRequest(name, email);
  }
}
