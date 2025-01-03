package de.turing85.quarkus.json.patch.spi;

import java.util.List;
import java.util.Optional;

import de.turing85.quarkus.json.patch.api.request.CreateUserRequest;

public interface UserDao {
  List<User> findAll();

  User create(final CreateUserRequest request);

  User findByName(final String name);

  void deleteByName(final String name);

  void deleteAll();

  Optional<User> update(final String name, final User user);
}
