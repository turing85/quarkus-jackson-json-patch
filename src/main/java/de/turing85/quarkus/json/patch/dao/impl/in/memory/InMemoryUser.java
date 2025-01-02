package de.turing85.quarkus.json.patch.dao.impl.in.memory;

import java.time.Instant;

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
public class InMemoryUser implements User {
  @lombok.Builder.Default
  String name = null;

  @lombok.Builder.Default
  String email = null;

  @lombok.EqualsAndHashCode.Exclude
  Instant createdAt = Instant.now();

  static InMemoryUser from(User user) {
    return new InMemoryUser(user.getName(), user.getEmail());
  }
}
