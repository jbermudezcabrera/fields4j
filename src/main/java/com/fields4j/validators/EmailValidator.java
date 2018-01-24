package com.fields4j.validators;

import com.fields4j.validators.core.ValidationException;

/** Clase para validar una dirección de correo. */
public class EmailValidator extends RegExpValidator {

  public EmailValidator() {
    this("");
    setMessage(getBundle().getString("emailError"));
  }

  public EmailValidator(String message) {
    super(".", message);
  }

  @Override
  public void validate(String value) throws ValidationException {
    org.apache.commons.validator.routines.EmailValidator validator =
        org.apache.commons.validator.routines.EmailValidator.getInstance(true, true);

    boolean isValid = validator.isValid(value);

    if (!isValid) {
      throw new ValidationException(getMessage());
    }
  }
}
