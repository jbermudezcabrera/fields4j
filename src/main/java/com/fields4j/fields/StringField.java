package com.fields4j.fields;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Objects;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicTextFieldUI;
import javax.swing.text.JTextComponent;

import com.fields4j.core.Field;
import com.fields4j.core.FieldStyle;
import com.fields4j.validators.LengthValidator;

public class StringField extends Field<JTextField, JTextField, String> {

  public static final int LEFT = JTextField.LEFT;
  public static final int RIGHT = JTextField.RIGHT;
  public static final int CENTER = JTextField.CENTER;
  public static final int LEADING = JTextField.LEADING;
  public static final int TRAILING = JTextField.TRAILING;

  private LengthValidator lengthValidator;
  private JTextFieldHintUI textFieldHintUI;

  public StringField() {
    super(new JTextField());

    textFieldHintUI = new JTextFieldHintUI(this);
    textFieldHintUI.setHintFont(getValueComponent().getFont().deriveFont(Font.ITALIC));

    getValueComponent().setUI(textFieldHintUI);

    setText(getClass().getSimpleName());

    lengthValidator = new LengthValidator(0, -1);
    addValidator(lengthValidator);

    getValueComponent().addFocusListener(new FocusAdapter() {
      @Override
      public void focusLost(FocusEvent e) {
        getValueComponent().setCaretPosition(0);
      }
    });

    getValueComponent().getDocument().addDocumentListener(new DocumentListener() {
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
  }

  public int getMaxLength() {
    return lengthValidator.getMaxLength();
  }

  public void setMaxLength(int value) {
    lengthValidator.setMaxLength(value);
  }

  /**
   * Devuelve la alineación horizontal del texto. Los valores válidos son:
   * <ul>
   * <li>{@code StringField.LEFT}
   * <li>{@code StringField.CENTER}
   * <li>{@code StringField.RIGHT}
   * <li>{@code StringField.LEADING}
   * <li>{@code StringField.TRAILING}
   * </ul>
   *
   * @return la alineación horizontal del texto
   */
  public int getHorizontalTextAlignment() {
    return getValueComponent().getHorizontalAlignment();
  }

  /**
   * Establece la alineación horizontal del texto. Los valores válidos son:
   * <ul>
   * <li>{@code StringField.LEFT}
   * <li>{@code StringField.CENTER}
   * <li>{@code StringField.RIGHT}
   * <li>{@code StringField.LEADING}
   * <li>{@code StringField.TRAILING}
   * </ul>
   *
   * @param horizontalAlignment la alineación horizontal del texto
   *
   * @throws IllegalArgumentException si {@code horizontalAlignment} no es un valor válido
   */
  public void setHorizontalTextAlignment(int horizontalAlignment) {
    getValueComponent().setHorizontalAlignment(horizontalAlignment);
  }

  /**
   * Devuelve el texto mostrado como sugerencia cuando el componente está fuera de foco y su valor
   * es vacío.
   */
  public String getPlaceholderText() {
    return textFieldHintUI.getHint();
  }

  /**
   * Establece un texto para mostrar con un formato de sugerencia cuando el componente esté fuera de
   * foco y su valor sea vacío.
   */
  public void setPlaceholderText(String placeholderText) {
    textFieldHintUI.setHint(placeholderText);
  }

}

final class JTextFieldHintUI extends BasicTextFieldUI implements FocusListener {

  private String hint;
  private Color hintColor;
  private Font hintFont;
  private Field field;

  JTextFieldHintUI(Field field) {
    this.field = Objects.requireNonNull(field);

    this.hint = "";
    this.hintColor = Color.GRAY;
    this.hintFont = null;
  }

  @Override
  public void installListeners() {
    super.installListeners();
    getComponent().addFocusListener(this);
  }

  @Override
  public void uninstallListeners() {
    super.uninstallListeners();
    getComponent().removeFocusListener(this);
  }

  @Override
  protected void paintSafely(Graphics g) {
    // Render the default text field UI
    super.paintSafely(g);

    // Render the hint text
    JTextComponent component = getComponent();

    FieldStyle fieldStyle = field.getFieldStyle();

    component.setForeground(fieldStyle.getValueComponentForeground());
    component.setFont(fieldStyle.getValueComponentFont());

    if (field.isEditable()) {
      component.setBackground(fieldStyle.getValueComponentBackground());
    } else {
      component.setBackground(UIManager.getDefaults().getColor("TextField.disabledBackground"));
    }

    if (component.getText().isEmpty() && !component.hasFocus()) {
      if (hintColor != null) {
        g.setColor(hintColor);
      }

      if (hintFont != null) {
        g.setFont(hintFont);
      }

      int padding = (component.getHeight() - component.getFont().getSize()) / 2;
      int inset = 3;

      g.drawString(hint, inset, component.getHeight() - padding - inset);
    }
  }

  @Override
  public void focusGained(FocusEvent e) {
    repaint();
  }

  @Override
  public void focusLost(FocusEvent e) {
    repaint();
  }

  String getHint() {
    return hint;
  }

  void setHint(String hint) {
    this.hint = hint;
  }

  Color getHintColor() {
    return hintColor;
  }

  void setHintColor(Color hintColor) {
    this.hintColor = hintColor;
  }

  Font getHintFont() {
    return hintFont;
  }

  void setHintFont(Font hintFont) {
    this.hintFont = hintFont;
  }

  private void repaint() {
    if (getComponent() != null) {
      getComponent().repaint();
    }
  }
}
