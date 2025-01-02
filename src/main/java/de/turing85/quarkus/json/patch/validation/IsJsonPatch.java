package de.turing85.quarkus.json.patch.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = JsonPatchValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface IsJsonPatch {
  String message() default "%s";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
