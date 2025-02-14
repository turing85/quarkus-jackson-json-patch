package de.turing85.quarkus.json.patch.dao.impl.in.memory;

import java.time.Instant;

import jakarta.validation.constraints.Email;

import de.turing85.quarkus.json.patch.spi.User;
import org.jspecify.annotations.Nullable;

// @formatter:off
public record InMemoryUser(String name, @Nullable @Email String email, Instant createdAt)
    implements User {
  public static InMemoryUser of(final User user) {
    return new InMemoryUser(user.name(), user.email());
  }

  private InMemoryUser(final String name, @Nullable @Email final String email) {
    this(name, email, Instant.now());
  }

  public InMemoryUser updateWith(final User user) {
    return new InMemoryUser(user.name(), user.email(), createdAt());
  }
}
// @formatter:on
