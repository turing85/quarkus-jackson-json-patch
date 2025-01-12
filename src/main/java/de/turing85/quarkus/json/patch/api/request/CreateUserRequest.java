package de.turing85.quarkus.json.patch.api.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import de.turing85.quarkus.json.patch.spi.User;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

// @formatter:off
@Schema(name = CreateUserRequest.SCHEMA_NAME, examples = { """
    {
      "name": "alice",
      "email": "alice@email.com"
    }""" })
public record CreateUserRequest(@NotNull String name, @Nullable @Email String email)
    implements User {
  public static final String SCHEMA_NAME = "Create User";

  public static CreateUserRequest of(final User user) {
    return new CreateUserRequest(user.name(), user.email());
  }

  public static CreateUserRequest of(final String name, @Nullable final String email) {
    return new CreateUserRequest(name, email);
  }
}
// @formatter:on
