package de.turing85.quarkus.json.patch.spi;

import org.jspecify.annotations.Nullable;

public interface User {
  String name();

  @Nullable
  String email();
}
