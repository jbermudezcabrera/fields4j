package com.fields4j.validators;

import com.fields4j.validators.core.ValidationException;
import com.google.common.collect.Lists;

public class AlphanumericValidator extends RegExpValidator {
  private static final String CONTAINS_VALID_CHARS = "[\\p{L}\\p{Digit}\\p{Space}_#-]+";
  private static final String CONTAINS_LETTERS = ".*\\p{L}+.*";

  public AlphanumericValidator() {
    this("");
    setMessage(getBundle().getString("alphaNumericError"));
  }

  public AlphanumericValidator(String message) {
    super(".*", message);
  }

  @Override
  public void validate(String value) throws ValidationException {
    AndValidator<String> andValidator =
        new AndValidator<>(
            Lists.newArrayList(
                new RegExpValidator(CONTAINS_VALID_CHARS), new RegExpValidator(CONTAINS_LETTERS)));

    andValidator.setMessage(getMessage());
    andValidator.validate(value);
  }
}
