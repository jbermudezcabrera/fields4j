package com.fields4j.validators;

import java.util.ResourceBundle;

import com.fields4j.validators.core.ValidationException;

public class PasswordValidator extends RegExpValidator {

  private boolean digitRequired;
  private boolean upperLetterRequired;
  private int minLength;

  /**
   * Contruye un validador que acepta la contraseña si tiene como mínimo 8 caracteres, contiene
   * dígitos y letras en mayúscula.
   */
  public PasswordValidator() {
    this("", 8, true, true);
    setMessage(getBundle().getString("passwordError"));
  }

  public PasswordValidator(String message) {
    this(message, 8, true, true);
  }

  /**
   * @param message             mensaje de error de validación
   * @param minLength           cantidad mínima de caracteres requerida
   * @param digitRequired       {@code true} si la contraseña debe contener al menos un dígito
   * @param upperLetterRequired {@code true} si la contraseña debe contener al menos una
   *                            letra mayúscula
   */
  public PasswordValidator(String message, int minLength, boolean digitRequired,
                           boolean upperLetterRequired) {
    super(".*", message);
    this.minLength = minLength;
    this.digitRequired = digitRequired;
    this.upperLetterRequired = upperLetterRequired;
  }

  public boolean isDigitRequired() {
    return digitRequired;
  }

  public void setDigitRequired(boolean digitRequired) {
    this.digitRequired = digitRequired;
  }

  public boolean isUpperLetterRequired() {
    return upperLetterRequired;
  }

  public void setUpperLetterRequired(boolean upperLetterRequired) {
    this.upperLetterRequired = upperLetterRequired;
  }

  public int getMinLength() {
    return minLength;
  }

  public void setMinLength(int minLength) {
    this.minLength = minLength;
  }

  @Override
  public void validate(String value) throws ValidationException {
    ResourceBundle bundle = getBundle();

    if (value.length() < minLength) {
      String format = bundle.getString("passwordLengthErrorFormat");
      throw new ValidationException(String.format(format, minLength));
    }

    if (digitRequired && !value.matches(".*\\d.*")) {
      throw new ValidationException(bundle.getString("passwordDigitError"));
    }

    if (upperLetterRequired && !value.matches(".*\\p{Lu}.*")) {
      throw new ValidationException(bundle.getString("passwordUppercaseError"));
    }
  }
}
