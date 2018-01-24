package com.fields4j.core;

import java.util.Objects;

/** Esta clase encapsula el resultado de un proceso de validación */
public class ValidationResult {

  private boolean valid;
  private Throwable cause;

  /** Crea un resultado de validación positivo, {@code isValid() == true} */
  public ValidationResult() {
    valid = true;
    cause = null;
  }

  /**
   * Crea un resultado de validación negativo, {@code isValid() == false} estableciendo {@code
   * cause} como fuente del error.
   */
  public ValidationResult(Throwable cause) {
    this.valid = false;
    this.cause = Objects.requireNonNull(cause);
  }

  /**
   * Devuelve la causa del error si este resultado es negativo {@code isValid() == false} o {@code
   * null} si es positivo.
   */
  public Throwable getCause() {
    return cause;
  }

  /**
   * Devuelve {@code true} solo si este {@code ValidationResult} representa un resultado sin errores
   * de validación.
   */
  public boolean isValid() {
    return valid;
  }
}
