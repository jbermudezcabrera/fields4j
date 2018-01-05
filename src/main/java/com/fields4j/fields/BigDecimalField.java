package com.fields4j.fields;

import com.fields4j.core.Field;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class BigDecimalField extends Field<JSpinner, JSpinner, BigDecimal> {
  public static DecimalFormat DECIMAL_FORMAT = null;

  public BigDecimalField() {
    super(new JSpinner(new BigDecimalSpinnerNumberModel()));

    setInitialValue(BigDecimal.valueOf(0));

    JSpinner spinner = getValueComponent();
    spinner.addChangeListener(e -> fireValueChangeEvent(null, null));

    spinner.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            spinner.transferFocus();

            if (getValue().equals(BigDecimal.ZERO)) {
              JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();
              JFormattedTextField textField = editor.getTextField();

              textField.setText("");
            }
          }
        });

    if (DECIMAL_FORMAT != null) {
      setFormat(DECIMAL_FORMAT);
    }
  }

  public DecimalFormat getFormat() {
    JSpinner spinner = getValueComponent();
    JSpinner.NumberEditor editor = (JSpinner.NumberEditor) spinner.getEditor();

    return editor.getFormat();
  }

  public void setFormat(DecimalFormat format) {
    JSpinner spinner = getValueComponent();
    JSpinner.NumberEditor editor;

    if (format == null) {
      editor = new JSpinner.NumberEditor(spinner);
    } else {
      editor = new JSpinner.NumberEditor(spinner, format.toPattern());

      /*
      establecer los simbolos del formato provisto ya que el NumberEditor crea un DecimalFormat
      con los símbolo por defecto.
      */
      editor.getFormat().setDecimalFormatSymbols(format.getDecimalFormatSymbols());
    }

    spinner.setEditor(editor);
  }

  public BigDecimal getMinimum() {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();

    if (model.getMinimum() == null) {
      return BigDecimal.valueOf(Integer.MIN_VALUE);
    }
    return (BigDecimal) model.getMinimum();
  }

  public void setMinimum(BigDecimal minimum) {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    model.setMinimum(minimum);
  }

  public BigDecimal getMaximum() {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();

    if (model.getMaximum() == null) {
      return BigDecimal.valueOf(Integer.MAX_VALUE);
    }
    return (BigDecimal) model.getMaximum();
  }

  public void setMaximum(BigDecimal maximum) {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    model.setMaximum(maximum);
  }

  public BigDecimal getStepSize() {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    return (BigDecimal) model.getStepSize();
  }

  public void setStepSize(BigDecimal stepSize) {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    model.setStepSize(stepSize);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public BigDecimal getValue() {
    return (BigDecimal) getValueComponent().getValue();
  }

  @Override
  public void setValue(BigDecimal value) {
    getValueComponent().setValue(value);
  }

  private static class BigDecimalSpinnerNumberModel extends SpinnerNumberModel {
    BigDecimalSpinnerNumberModel() {
      super(BigDecimal.valueOf(0), null, null, BigDecimal.valueOf(1));
    }

    @Override
    public Object getNextValue() {
      BigDecimal newValue = ((BigDecimal) getValue()).add((BigDecimal) getStepSize());

      Comparable<BigDecimal> maximum = getMaximum();
      Comparable<BigDecimal> minimum = getMinimum();

      if ((maximum != null) && (maximum.compareTo(newValue) < 0)) {
        return null;
      }

      if ((minimum != null) && (minimum.compareTo(newValue) > 0)) {
        return null;
      }

      return newValue;
    }

    @Override
    public Object getPreviousValue() {
      BigDecimal newValue = ((BigDecimal) getValue()).subtract((BigDecimal) getStepSize());

      Comparable<BigDecimal> maximum = getMaximum();
      Comparable<BigDecimal> minimum = getMinimum();

      if ((maximum != null) && (maximum.compareTo(newValue) < 0)) {
        return null;
      }

      if ((minimum != null) && (minimum.compareTo(newValue) > 0)) {
        return null;
      }

      return newValue;
    }
  }
}
