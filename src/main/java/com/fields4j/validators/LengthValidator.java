package com.fields4j.validators;

import com.fields4j.validators.core.BaseValidator;
import com.fields4j.validators.core.ValidationException;

import java.util.ResourceBundle;

/**
 * Clase que realiza una validación de la longitud mínima y máxima de una cadena.
 */
public class LengthValidator extends BaseValidator<String> {
  private int minLength;
  private int maxLength;

  /**
   * Construye un nuevo validador que no impone límites a las longitudes mínimas y máximas de las
   * cadenas.
   */
  public LengthValidator() {
    this(-1, 0);
  }

  /**
   * Construye un nuevo validador con las longitudes mínimas y máximas especificadas.
   *
   * @param minLength longitud mínima de la cadena
   * @param maxLength longitud máxima de la cadena, un valor de -1 indica que la cadena no tiene
   *                  límite de longitud
   */
  public LengthValidator(int minLength, int maxLength) {
    super("");
    this.maxLength = maxLength;
    this.minLength = minLength;
  }

  @Override
  public void validate(String value) throws ValidationException {
    ResourceBundle bundle = getBundle();

    boolean valid = value.length() >= minLength;

    if (!valid) {
      String format = bundle.getString("lengthMinErrorFormat");
      throw new ValidationException(String.format(format, minLength));
    }

    if (maxLength > 0) {
      valid = value.length() <= maxLength;
    }

    if (!valid) {
      String format = bundle.getString("lengthMaxErrorFormat");
      throw new ValidationException(String.format(format, maxLength));
    }
  }

  public int getMinLength() {
    return minLength;
  }

  public void setMinLength(int minLength) {
    this.minLength = minLength;
  }

  public int getMaxLength() {
    return maxLength;
  }

  public void setMaxLength(int maxLength) {
    this.maxLength = maxLength;
  }
}
