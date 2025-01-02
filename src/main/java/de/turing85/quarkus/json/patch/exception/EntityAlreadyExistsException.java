package de.turing85.quarkus.json.patch.exception;

public class EntityAlreadyExistsException extends RuntimeException {
  private EntityAlreadyExistsException(String message) {
    super(message);
  }

  public static EntityAlreadyExistsException of(String message) {
    return new EntityAlreadyExistsException(message);
  }
}
