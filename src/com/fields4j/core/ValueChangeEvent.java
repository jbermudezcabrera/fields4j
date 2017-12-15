package com.fields4j.core;

import java.util.Objects;

/**
 * Un {@code ValueChangeEvent} se crea cuando un {@code Field} cambia su valor. Un objeto {@code
 * ValueChangeEvent} es enviado como argumento a los métodos de {@code ValueChangeListener}.
 * Normalmente los {@code ValueChangeEvent} van acompañados por el {@code Field} donde ocurrió el
 * cambio y el valor viejo y nuevo de dicho campo.
 *
 * @param <V> el tipo de los valores esperados
 */
public class ValueChangeEvent<V> {

  private final Field<?, ?, V> source;
  private final V oldValue;
  private final V newValue;

  /**
   * Construye un nuevo {@code ValueChangeEvent} que describe un cambio de valor de {@code oldValue}
   * para {@code newValue} ocurrido en {@code source}.
   *
   * <p>Se pueden proveer valores nulos para {@code oldValue} y {@code newValue} si sus valores
   * reales son desconocidos. Sin embargo, si {@code newValue} es {@code null} se consultará el
   * método {@link Field#getValue()} para asignarle el valor actual de {@code source}. Esto
   * quiere decir que {@link #getNewValue()} devolverá {@code null} solamente si ese es el nuevo
   * valor del campo.
   *
   * @param source campo donde se originó el evento
   * @param oldValue valor antes del cambio, puede ser {@code null}
   * @param newValue nuevo valor, puede ser {@code null}
   */
  public ValueChangeEvent(Field<?, ?, V> source, V oldValue, V newValue) {
    this.source = Objects.requireNonNull(source);

    this.oldValue = oldValue;
    this.newValue = (newValue == null) ? source.getValue() : newValue;
  }

  public Field<?, ?, V> getSource() {
    return source;
  }

  public V getOldValue() {
    return oldValue;
  }

  public V getNewValue() {
    return newValue;
  }
}
