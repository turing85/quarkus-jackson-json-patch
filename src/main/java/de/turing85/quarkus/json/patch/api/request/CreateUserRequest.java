package de.turing85.quarkus.json.patch.api.request;

import de.turing85.quarkus.json.patch.api.response.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CreateUserRequest {
  String name;
  String email;

  public static CreateUserRequest from(User user) {
    return CreateUserRequest.builder().name(user.getName()).email(user.getEmail()).build();
  }
}
