package de.turing85.quarkus.json.patch.spi;

import java.util.List;

import de.turing85.quarkus.json.patch.api.request.CreateUserRequest;

public interface UserDao {
  List<User> findAll();

  User create(CreateUserRequest request);

  User create(User user);

  User findByName(String name);

  void delete(User user);

  void deleteByName(String name);

  void deleteAll();
}
