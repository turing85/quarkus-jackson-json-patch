package de.turing85.quarkus.json.patch.spi;

import java.util.List;

import de.turing85.quarkus.json.patch.api.request.CreateUserRequest;

public interface UserDao {
  List<User> findAll();

  User create(CreateUserRequest request);

  User findByName(String name);

  void deleteByName(String name);

  void deleteAll();

  User update(String name, User user);
}
