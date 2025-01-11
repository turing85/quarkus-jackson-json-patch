package de.turing85.quarkus.json.patch.api.response;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

import de.turing85.quarkus.json.patch.spi.User;
import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.jspecify.annotations.Nullable;

@Schema(name = UserResponse.SCHEMA_NAME)
@RegisterForReflection
public record UserResponse(@NotNull String name, @Nullable @Email String email) implements User {
  public static final String SCHEMA_NAME = "User";
  public static final String SCHEMA_NAME_LIST = "User List";

  public static UserResponse of(final User user) {
    return new UserResponse(user.name(), user.email());
  }

  public static UserResponse of(final String name, @Nullable final String email) {
    return new UserResponse(name, email);
  }
}

@Schema(name = UserResponse.SCHEMA_NAME_LIST, ref = UserResponse.SCHEMA_NAME,
    type = SchemaType.ARRAY)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("unused")
class UserList {}