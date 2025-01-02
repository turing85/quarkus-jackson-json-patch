package de.turing85.quarkus.json.patch.dao.impl.in.memory;

import de.turing85.quarkus.json.patch.api.response.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;

@Value
@Jacksonized
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class InMemoryUser implements User {
  @lombok.Builder.Default
  String name = null;

  @lombok.Builder.Default
  String email = null;

  static InMemoryUser from(User user) {
    return new InMemoryUser(user.getName(), user.getEmail());
  }
}
