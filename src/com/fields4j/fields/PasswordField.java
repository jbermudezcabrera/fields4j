package com.fields4j.fields;

import com.fields4j.FieldUtils;
import com.fields4j.core.Field;
import com.fields4j.validators.PasswordValidator;
import com.fields4j.validators.core.ValidationException;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;

public class PasswordField extends Field<JPanel, JPasswordField, String> {
  private PasswordValidator passwordValidator;
  private JButton           suggestPasswordBtn;
  private ActionListener listener = null;

  public PasswordField() {
    super(new JPanel(), new JPasswordField());

    passwordValidator = new PasswordValidator();
    addValidator(passwordValidator);

    initPasswordField();
    initButton();

    getMainComponent().setLayout(new BorderLayout(5, 5));
    getMainComponent().add(getValueComponent(), BorderLayout.CENTER);
    getMainComponent().add(suggestPasswordBtn, BorderLayout.EAST);

    setPasswordSuggestionEnabled(false);
  }

  public void setPasswordSuggestionEnabled(boolean enabled) {
    suggestPasswordBtn.setVisible(enabled);
  }

  @Override
  public boolean isEmpty() {
    return getValue().isEmpty();
  }

  @Override
  public String getValue() {
    char[] characters = getValueComponent().getPassword();
    String password   = String.valueOf(characters);

    // recomendado por la documentacion del metodo getPassword()
    Arrays.fill(characters, '0');
    return password;
  }

  @Override
  public void setValue(String value) {
    getValueComponent().setText(value);
  }

  @Override
  public boolean isEditable() {
    return getValueComponent().isEditable();
  }

  @Override
  public void setEditable(boolean editable) {
    getValueComponent().setEditable(editable);

    validateField();
  }

  public ActionListener getSuggestPasswordListener() {
    return listener;
  }

  public void setSuggestPasswordListener(ActionListener listener) {
    if (this.listener != null) {
      suggestPasswordBtn.removeActionListener(this.listener);
    }

    this.listener = listener;
    suggestPasswordBtn.addActionListener(this.listener);
  }

  private void initButton() {
    String iconPath         = "/com/fields4j/resources/images/generarClave.png";
    String rolloverIconPath = "/com/fields4j/resources/images/generarClave-rollover.png";

    suggestPasswordBtn = new JButton(FieldUtils.getIcon(iconPath));
    suggestPasswordBtn.setRolloverIcon(FieldUtils.getIcon(rolloverIconPath));

    ResourceBundle bundle = ResourceBundle.getBundle("com/fields4j/resources/PasswordField");

    suggestPasswordBtn.setToolTipText(bundle.getString("passwordSuggestionButton.tooltip"));

    setSuggestPasswordListener(e -> {
      String suggestion = getPasswordSuggestion();
      String format     = bundle.getString("passwordSuggestionFormat");

      FieldUtils.showInfo(String.format(format, suggestion));
      setValue(suggestion);
    });
  }

  private void initPasswordField() {
    JPasswordField passwordField = getValueComponent();
    passwordField.getDocument().addDocumentListener(new DocumentListener() {
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

  /**
   * Genera una contraseña aleatoria de letras (minúsculas y/o mayusculas) y números
   */
  private String getPasswordSuggestion() {
    while (true) {
      char[] password = new char[passwordValidator.getMinLength()];

      String letters = "abcdefghijklmnpqrstuvwxyz";
      String digits  = "123456789";

      if (passwordValidator.isUpperLetterRequired()) {
        letters = letters.toUpperCase();
      }

      char[] characters = (letters + digits).toCharArray();
      Random random     = new Random();

      for (int i = 0; i < password.length; i++) {
        int charIndex = random.nextInt(characters.length);
        password[i] = characters[charIndex];
      }

      try {
        String passwordStr = String.valueOf(password);
        passwordValidator.validate(passwordStr);

        return passwordStr;
      }
      catch (ValidationException ignored) {
      }
    }
  }
}
