package com.fields4j.validators;

import com.fields4j.validators.core.BaseValidator;
import com.fields4j.validators.core.ValidationException;
import com.fields4j.validators.core.Validator;

import java.util.Objects;

public class AndValidator<T> extends BaseValidator<T> {

  private Iterable<Validator<T>> validators;

  /**
   * Crea un {@code Validator} que solo acepta un valor si todos los {@code Validator} suministrados
   * lo aceptan.
   *
   * @param validators los {@code Validator} a verificar
   */
  public AndValidator(Iterable<Validator<T>> validators) {
    this.validators = Objects.requireNonNull(validators);
  }

  @Override
  public void validate(T value) throws ValidationException {
    for (Validator<T> validator : validators) {
      validator.validate(value);
    }
  }
}
