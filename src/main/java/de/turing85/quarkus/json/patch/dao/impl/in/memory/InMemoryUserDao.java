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
  public User create(CreateUserRequest request) {
    if (USERS.stream().anyMatch(u -> u.getName().equals(request.getName()))) {
      throw EntityAlreadyExistsException
          .of("User with name \"%s\" already exists".formatted(request.getName()));
    }
    // @formatter:off
    final User user = InMemoryUser.builder()
        .name(request.getName())
        .email(request.getEmail())
        .build();
    return create(user);
  // @formatter:on
  }

  public User create(User user) {
    final InMemoryUser created = InMemoryUser.from(user);
    USERS.add(created);
    return created;
  }

  @Override
  public User findByName(String name) {
    // @formatter:off
    return USERS.stream()
        .filter(user -> user.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new NoSuchElementException("User not found"));
    // @formatter:on
  }

  @Override
  public void delete(User user) {
    USERS.remove(InMemoryUser.from(user));
  }

  @Override
  public void deleteByName(String name) {
    USERS.stream().filter(user -> user.getName().equals(name)).toList().forEach(USERS::remove);
  }

  @Override
  public void deleteAll() {
    USERS.clear();
  }
}
