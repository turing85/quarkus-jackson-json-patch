package de.turing85.quarkus.json.patch.api.request;

import de.turing85.quarkus.json.patch.spi.User;

public record CreateUserRequest(String name, String email) implements User {
  public static CreateUserRequest from(final User user) {
    return new CreateUserRequest(user.name(), user.email());
  }
}
