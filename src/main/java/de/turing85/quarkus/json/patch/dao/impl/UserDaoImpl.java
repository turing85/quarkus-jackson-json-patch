package de.turing85.quarkus.json.patch.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.enterprise.context.ApplicationScoped;

import de.turing85.quarkus.json.patch.api.User;
import de.turing85.quarkus.json.patch.api.UserDao;

@ApplicationScoped
public class UserDaoImpl implements UserDao {
  // @formatter:off
  private static final List<User> USERS =
      new ArrayList<>(List.of(
          UserDto.builder()
              .name("alice")
              .email("alice@gmail.com")
              .build(),
          UserDto.builder()
              .name("bob")
              .email("bob@gmail.com")
              .build(),
          UserDto.builder()
              .name("claire")
              .email("claire@gmail.com")
              .build()));
  // @formatter:on

  @Override
  public List<User> findAll() {
    return Collections.unmodifiableList(USERS);
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
  public void add(User user) {
    USERS.add(user);
  }

  @Override
  public void delete(User user) {
    USERS.remove(user);
  }
}
