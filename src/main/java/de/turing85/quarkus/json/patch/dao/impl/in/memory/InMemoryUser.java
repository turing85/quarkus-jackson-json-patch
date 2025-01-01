package de.turing85.quarkus.json.patch.dao.impl.in.memory;

import de.turing85.quarkus.json.patch.api.User;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
@EqualsAndHashCode
@ToString
public final class InMemoryUser implements User {
  @lombok.Builder.Default
  private final String name = null;

  @lombok.Builder.Default
  private final String email = null;

  static InMemoryUser from(User user) {
    return new InMemoryUser(user.getName(), user.getEmail());
  }
}
