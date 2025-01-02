package de.turing85.quarkus.json.patch.api.response;

import de.turing85.quarkus.json.patch.spi.User;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
public class UserResponse {
  String name;
  String email;

  public static UserResponse from(User user) {
    // @formatter:off
    return UserResponse.builder()
        .name(user.getName())
        .email(user.getEmail())
        .build();
    // @formatter:on
  }
}
