package de.turing85.quarkus.json.patch.dao.impl.in.memory;

import java.time.Instant;

import de.turing85.quarkus.json.patch.spi.User;

public record InMemoryUser(String name, String email, Instant createdAt) implements User {
  InMemoryUser(final User user) {
    this(user.name(), user.email());
  }

  public InMemoryUser(final String name, final String email) {
    this(name, email, Instant.now());
  }

  public InMemoryUser updateWith(final User user) {
    return new InMemoryUser(user.name(), user.email(), createdAt());
  }
}
