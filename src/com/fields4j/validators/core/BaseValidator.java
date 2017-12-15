package com.fields4j.validators.core;

import java.util.ResourceBundle;

/** Implementa funcionalidades básicas para la mayoría de los validadores. */
public abstract class BaseValidator <T> implements Validator<T> {
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
      "com/fields4j/validators/resources/Validator");

  private String message;

  protected BaseValidator() {
    this(BUNDLE.getString("defaultError"));
  }

  protected BaseValidator(String message) {
    this.message = message;
  }

  @Override
  public String getMessage() {
    return message;
  }

  @Override
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Provee acceso a un {@link ResourceBundle} que puede ser común para todas las subclases.
   *
   * @return el {@link ResourceBundle} asociado a esta clase
   */
  public ResourceBundle getBundle() {
    return BUNDLE;
  }
}
