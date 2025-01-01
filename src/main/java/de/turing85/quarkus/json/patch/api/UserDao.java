package de.turing85.quarkus.json.patch.api;

import java.util.List;

public interface UserDao {
  List<User> findAll();

  User findByName(String name);

  void add(User user);

  void delete(User user);
}
