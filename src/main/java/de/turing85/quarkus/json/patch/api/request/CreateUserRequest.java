package de.turing85.quarkus.json.patch.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import de.turing85.quarkus.json.patch.spi.User;
import org.jspecify.annotations.Nullable;

public record CreateUserRequest(@NotNull String name, @Nullable @Email String email)
    implements User {
  public static CreateUserRequest of(final User user) {
    return new CreateUserRequest(user.name(), user.email());
  }

  public static CreateUserRequest of(final String name, @Nullable final String email) {
    return new CreateUserRequest(name, email);
  }
}
