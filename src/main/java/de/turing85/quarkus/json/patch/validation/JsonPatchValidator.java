package de.turing85.quarkus.json.patch.validation;

import jakarta.inject.Singleton;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.InvalidJsonPatchException;

@Singleton
final class JsonPatchValidator implements ConstraintValidator<JsonPatch, JsonNode> {
  @Override
  public boolean isValid(final JsonNode value, final ConstraintValidatorContext context) {
    try {
      com.flipkart.zjsonpatch.JsonPatch.validate(value);
      return true;
    } catch (final InvalidJsonPatchException e) {
      context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation()
          .disableDefaultConstraintViolation();
      return false;
    }
  }
}
