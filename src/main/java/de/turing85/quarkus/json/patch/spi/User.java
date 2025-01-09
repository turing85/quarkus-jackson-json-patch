package de.turing85.quarkus.json.patch.spi;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
@SuppressWarnings("unused")
public interface User {
  String name();

  @Nullable
  String email();
}
