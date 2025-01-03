package de.turing85.quarkus.json.patch.dao.impl.in.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

import de.turing85.quarkus.json.patch.api.request.CreateUserRequest;
import de.turing85.quarkus.json.patch.spi.User;
import de.turing85.quarkus.json.patch.spi.UserDao;
import de.turing85.quarkus.json.patch.spi.exception.EntityAlreadyExistsException;

@ApplicationScoped
public class InMemoryUserDao implements UserDao {
  private static final List<InMemoryUser> USERS = new ArrayList<>();

  @Override
  public List<User> findAll() {
    return Collections.unmodifiableList(USERS);
  }

  @Override
  public InMemoryUser create(final CreateUserRequest request) {
    if (USERS.stream().anyMatch(user -> user.name().equals(request.name()))) {
      throw EntityAlreadyExistsException
          .of("User with name \"%s\" already exists".formatted(request.name()));
    }
    final InMemoryUser created = new InMemoryUser(request);
    USERS.add(created);
    return created;
  }

  @Override
  public InMemoryUser findByName(final String name) {
    // @formatter:off
    return USERS.stream()
        .filter(user -> user.name().equals(name))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("User not found"));
    // @formatter:on
  }

  @Override
  public void deleteByName(final String name) {
    // @formatter:off
    USERS.stream()
        .filter(user -> user.name().equals(name))
        .toList()
        .forEach(USERS::remove);
    // @formatter:on
  }

  @Override
  public Optional<User> update(final String name, final User user) {
    final InMemoryUser oldUser = findByName(name);
    final InMemoryUser newUser = oldUser.updateWith(user);
    if (!Objects.equals(oldUser, newUser)) {
      USERS.remove(oldUser);
      USERS.add(newUser);
      return Optional.of(newUser);
    }
    return Optional.empty();
  }

  @Override
  public void deleteAll() {
    USERS.clear();
  }
}
