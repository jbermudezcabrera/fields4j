package com.fields4j.fields;

import com.fields4j.core.Field;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.text.Format;
import java.util.function.Function;

public class ReadOnlyField extends Field<JFormattedTextField, JFormattedTextField, Object> {
  public static final int LEFT = JFormattedTextField.LEFT;
  public static final int RIGHT = JFormattedTextField.RIGHT;
  public static final int CENTER = JFormattedTextField.CENTER;
  public static final int LEADING = JFormattedTextField.LEADING;
  public static final int TRAILING = JFormattedTextField.TRAILING;

  private Function<Object, String> toStringFunction = null;

  public ReadOnlyField() {
    super(new JFormattedTextField());

    JFormattedTextField valueComponent = getValueComponent();

    valueComponent.setEditable(false);
    valueComponent.setFocusable(false);

    PropertyChangeListener listener = e -> fireValueChangeEvent(e.getOldValue(), e.getNewValue());

    valueComponent.addPropertyChangeListener("value", listener);
  }

  public ReadOnlyField(Format format) {
    super(new JFormattedTextField(format));

    JFormattedTextField valueComponent = getValueComponent();

    valueComponent.setEditable(false);
    valueComponent.setFocusable(false);

    PropertyChangeListener listener = e -> fireValueChangeEvent(e.getOldValue(), e.getNewValue());

    valueComponent.addPropertyChangeListener("value", listener);
  }

  @Override
  public boolean isEmpty() {
    Object value = getValue();
    return (value == null) || value.toString().isEmpty();
  }

  @Override
  public Object getValue() {
    return getValueComponent().getValue();
  }

  @Override
  public void setValue(Object value) {
    getValueComponent().setValue(value);

    if (toStringFunction != null) {
      getValueComponent().setText(toStringFunction.apply(value));
    }
  }

  @Override
  public boolean isEditable() {
    return false;
  }

  @Override
  public void setEditable(boolean editable) {}

  /**
   * Devuelve la alineación horizontal del texto. Los valores válidos son:
   *
   * <ul>
   *   <li>{@code ReadOnlyField.LEFT}
   *   <li>{@code ReadOnlyField.CENTER}
   *   <li>{@code ReadOnlyField.RIGHT}
   *   <li>{@code ReadOnlyField.LEADING}
   *   <li>{@code ReadOnlyField.TRAILING}
   * </ul>
   *
   * @return la alineación horizontal del texto
   */
  public int getHorizontalTextAlignment() {
    return getValueComponent().getHorizontalAlignment();
  }

  /**
   * Establece la alineación horizontal del texto. Los valores válidos son:
   *
   * <ul>
   *   <li>{@code ReadOnlyField.LEFT}
   *   <li>{@code ReadOnlyField.CENTER}
   *   <li>{@code ReadOnlyField.RIGHT}
   *   <li>{@code ReadOnlyField.LEADING}
   *   <li>{@code ReadOnlyField.TRAILING}
   * </ul>
   *
   * @param horizontalAlignment la alineación horizontal del texto
   * @exception IllegalArgumentException si {@code horizontalAlignment} no es un valor válido
   */
  public void setHorizontalTextAlignment(int horizontalAlignment) {
    getValueComponent().setHorizontalAlignment(horizontalAlignment);
  }

  /**
   * Devuelve la función usada para obtener una representación textual del valor del campo, puede
   * ser {@code null}, indicando que se está empleando el comportamiento por defecto.
   *
   * @return la función que se esta usando o {@code null}
   */
  public Function<Object, String> getToStringFunction() {
    return toStringFunction;
  }

  /**
   * Establece un forma personalizada de representar el valor contenido en el campo. A la hora de
   * representar el valor se le pasa este a {@code toStringFunction} para obtener la cadena que se
   * debe mostrar.
   *
   * @param toStringFunction función a emplear para obtener la representación textual del valor del
   *     campo, si es {@code null} establece el comportamiento por defecto.
   */
  public void setToStringFunction(Function<Object, String> toStringFunction) {
    this.toStringFunction = (toStringFunction == null) ? Object::toString : toStringFunction;
  }
}
