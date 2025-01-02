package de.turing85.quarkus.json.patch.dao.impl.in.memory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

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
  public InMemoryUser create(CreateUserRequest request) {
    if (USERS.stream().anyMatch(u -> u.name().equals(request.name()))) {
      throw EntityAlreadyExistsException
          .of("User with name \"%s\" already exists".formatted(request.name()));
    }
    final InMemoryUser created = new InMemoryUser(request);
    USERS.add(created);
    return created;
  }

  @Override
  public InMemoryUser findByName(String name) {
    // @formatter:off
    return USERS.stream()
        .filter(user -> user.name().equals(name))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("User not found"));
    // @formatter:on
  }

  @Override
  public void deleteByName(String name) {
    USERS.stream().filter(user -> user.name().equals(name)).toList().forEach(USERS::remove);
  }

  @Override
  public InMemoryUser update(String name, User user) {
    InMemoryUser oldUser = findByName(name);
    InMemoryUser newUser = new InMemoryUser(user, oldUser.createdAt());
    USERS.remove(oldUser);
    USERS.add(newUser);
    return newUser;
  }

  @Override
  public void deleteAll() {
    USERS.clear();
  }
}
