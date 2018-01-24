package com.fields4j.validators;

import java.util.Objects;

import com.fields4j.validators.core.BaseValidator;
import com.fields4j.validators.core.ValidationException;
import com.fields4j.validators.core.Validator;

public class OrValidator<T> extends BaseValidator<T> {

  private Iterable<Validator<T>> validators;

  /**
   * Crea un {@code Validator} que acepta un valor si al menos uno de los {@code Validator}
   * suministrados lo acepta.
   *
   * @param validators los {@code Validator} a verificar
   */
  public OrValidator(Iterable<Validator<T>> validators) {
    this.validators = Objects.requireNonNull(validators);
  }

  @Override
  public void validate(T value) throws ValidationException {
    ValidationException firstError = null;

    for (Validator<T> validator : validators) {
      try {
        validator.validate(value);
        return;
      }
      catch (ValidationException e) {
        if (firstError == null) {
          firstError = e;
        }
      }
    }

    if (firstError != null) {
      throw firstError;
    }
  }
}
