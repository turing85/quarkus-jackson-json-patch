package de.turing85.quarkus.json.patch.api.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import de.turing85.quarkus.json.patch.spi.User;
import io.quarkus.runtime.annotations.RegisterForReflection;
import org.jspecify.annotations.Nullable;

@RegisterForReflection
public record UserResponse(@NotNull String name, @Nullable @Email String email) implements User {
  public static UserResponse of(final User user) {
    return new UserResponse(user.name(), user.email());
  }

  public static UserResponse of(final String name, @Nullable final String email) {
    return new UserResponse(name, email);
  }
}
