package com.fields4j.validators;

import com.fields4j.validators.core.ValidationException;
import org.apache.commons.validator.routines.InetAddressValidator;

/** Verifica que el valor tenga el formato de una dirección IPv4. */
public class IPv4Validator extends RegExpValidator {

  public IPv4Validator() {
    this("");
    setMessage(getBundle().getString("ipV4Error"));
  }

  public IPv4Validator(String message) {
    super(".", message);
  }

  @Override
  public void validate(String value) throws ValidationException {
    InetAddressValidator validator = InetAddressValidator.getInstance();

    boolean isValid = validator.isValidInet4Address(value);

    if (!isValid) {
      throw new ValidationException(getMessage());
    }
  }
}
