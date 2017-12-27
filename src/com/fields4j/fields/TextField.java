package com.fields4j.fields;

import com.fields4j.core.Field;
import com.fields4j.validators.LengthValidator;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

public class TextField extends Field<JScrollPane, JTextArea, String> {

  private LengthValidator lengthValidator;

  public TextField() {
    super(new JScrollPane(), new JTextArea());
    lengthValidator = new LengthValidator();

    setText(getClass().getSimpleName());
    addValidator(lengthValidator);

    getMainComponent().setViewportView(getValueComponent());
    getMainComponent().setBorder(BorderFactory.createEmptyBorder());

    getValueComponent().setLineWrap(true);
    getValueComponent().setWrapStyleWord(true);
    getValueComponent().setRows(3);

    // transfer focus when tab is pressed instead of print the tab character
    getValueComponent().setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, null);
    getValueComponent().setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, null);

    defaultBorder = new JTextField().getBorder();
    // establecer el nuevo borde
    resetState();

    getValueComponent()
        .getDocument()
        .addDocumentListener(
            new DocumentListener() {
              @Override
              public void insertUpdate(DocumentEvent e) {
                fireValueChangeEvent(null, getValue());
              }

              @Override
              public void removeUpdate(DocumentEvent e) {
                fireValueChangeEvent(null, getValue());
              }

              @Override
              public void changedUpdate(DocumentEvent e) {
                fireValueChangeEvent(null, getValue());
              }
            });
  }

  @Override
  public boolean isEmpty() {
    return getValue().isEmpty();
  }

  @Override
  public String getValue() {
    return getValueComponent().getText();
  }

  @Override
  public void setValue(String value) {
    getValueComponent().setText(value);
  }

  public int getMinLength() {
    return lengthValidator.getMinLength();
  }

  public void setMinLength(int value) {
    lengthValidator.setMinLength(value);

    validateField();
  }

  public int getMaxLength() {
    return lengthValidator.getMaxLength();
  }

  public void setMaxLength(int value) {
    lengthValidator.setMaxLength(value);

    validateField();
  }

  @Override
  public void resetState() {
    setValue("");
    super.resetState();
  }
}
