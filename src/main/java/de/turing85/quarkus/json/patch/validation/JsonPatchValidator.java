package de.turing85.quarkus.json.patch.validation;

import jakarta.inject.Singleton;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.InvalidJsonPatchException;
import com.flipkart.zjsonpatch.JsonPatch;

@Singleton
class JsonPatchValidator implements ConstraintValidator<IsJsonPatch, JsonNode> {
  @Override
  public boolean isValid(JsonNode value, ConstraintValidatorContext context) {
    try {
      JsonPatch.validate(value);
      return true;
    } catch (InvalidJsonPatchException e) {
      context.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation()
          .disableDefaultConstraintViolation();
      return false;
    }
  }
}
