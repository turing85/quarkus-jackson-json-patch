package de.turing85.quarkus.json.patch.dao.impl.in.memory;

import java.time.Instant;

import de.turing85.quarkus.json.patch.spi.User;

public record InMemoryUser(String name, String email, Instant createdAt) implements User {

  InMemoryUser(User user) {
    this(user.name(), user.email());
  }

  InMemoryUser(User user, Instant createdAt) {
    this(user.name(), user.email(), createdAt);
  }

  public InMemoryUser(String name, String email) {
    this(name, email, Instant.now());
  }
}
