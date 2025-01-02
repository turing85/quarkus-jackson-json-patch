package de.turing85.quarkus.json.patch.api.response;

import de.turing85.quarkus.json.patch.spi.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserResponse {
  String name;
  String email;

  public static UserResponse from(User user) {
    return new UserResponse(user.getName(), user.getEmail());
  }
}
