package com.fields4j.validators;

import com.fields4j.validators.core.BaseValidator;
import com.fields4j.validators.core.ValidationException;

/**
 * Verifica que un puerto se encuentre en el rango numérico válido. No se chequea que el número
 * sea algún puerto reservado o con algún propósito específico.
 */
public class PortValidator extends BaseValidator<Integer> {

  public PortValidator() {
    this("");
    setMessage(getBundle().getString("portRangeError"));
  }

  public PortValidator(String message) {
    super(message);
  }

  @Override
  public void validate(Integer value) throws ValidationException {
    if ((value < 0) || (value > 65535)) {
      throw new ValidationException(getMessage());
    }
  }
}
