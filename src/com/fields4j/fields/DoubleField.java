package com.fields4j.fields;

import com.fields4j.core.Field;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class DoubleField extends Field<JSpinner, JSpinner, Double> {
  public DoubleField() {
    super(new JSpinner(new SpinnerNumberModel(0.0, null, null, 1)));
    setInitialValue(0.0);

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
  }

  public double getMinimum() {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    return (model.getMinimum() == null) ? Double.NaN : (double) model.getMinimum();
  }

  public void setMinimum(double minimum) {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    model.setMinimum(minimum);
  }

  public double getMaximum() {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    return (model.getMaximum() == null) ? Double.NaN : (double) model.getMaximum();
  }

  public void setMaximum(double maximum) {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    model.setMaximum(maximum);
  }

  public double getStepSize() {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    return (double) model.getStepSize();
  }

  public void setStepSize(double stepSize) {
    SpinnerNumberModel model = (SpinnerNumberModel) getValueComponent().getModel();
    model.setStepSize(stepSize);
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public Double getValue() {
    return (Double) getValueComponent().getValue();
  }

  @Override
  public void setValue(Double value) {
    getValueComponent().setValue(value);
  }
}
