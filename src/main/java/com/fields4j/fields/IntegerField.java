package com.fields4j.fields;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DecimalFormat;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.fields4j.core.Field;

public class IntegerField extends Field<JSpinner, JSpinner, Integer> {

  public static DecimalFormat DECIMAL_FORMAT = null;

  public IntegerField() {
    super(new JSpinner(new SpinnerNumberModel(0, null, null, 1)));
    setInitialValue(0);

    JSpinner spinner = getValueComponent();
    spinner.addChangeListener(e -> fireValueChangeEvent(null, null));

    spinner.addFocusListener(
        new FocusAdapter() {
          @Override
          public void focusGained(FocusEvent e) {
            spinner.transferFocus();

            if (0 == getValue()) {
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

  public int getMinimum() {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    return (model.getMinimum() == null) ? Integer.MIN_VALUE : (int) model.getMinimum();
  }

  public void setMinimum(int minimum) {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    model.setMinimum(minimum);

    if (getValue() < minimum) {
      setValue(minimum);
    }
  }

  public int getMaximum() {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    return (model.getMaximum() == null) ? Integer.MAX_VALUE : (int) model.getMaximum();
  }

  public void setMaximum(int maximum) {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    model.setMaximum(maximum);

    if (getValue() > maximum) {
      setValue(maximum);
    }
  }

  public int getStepSize() {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    return (int) model.getStepSize();
  }

  public void setStepSize(int stepSize) {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    model.setStepSize(stepSize);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public Integer getValue() {
    return (Integer) getValueComponent().getValue();
  }

  @Override
  public void setValue(Integer value) {
    getValueComponent().setValue(value);
  }
}
