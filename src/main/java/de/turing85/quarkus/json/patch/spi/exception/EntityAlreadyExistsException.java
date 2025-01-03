package de.turing85.quarkus.json.patch.spi.exception;

public final class EntityAlreadyExistsException extends RuntimeException {
  private EntityAlreadyExistsException(final String message) {
    super(message);
  }

  public static EntityAlreadyExistsException of(final String message) {
    return new EntityAlreadyExistsException(message);
  }
}
