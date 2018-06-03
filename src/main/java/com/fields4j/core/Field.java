package com.fields4j.core;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.TextField;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JSpinner;
import javax.swing.border.Border;

import com.fields4j.validators.core.ValidationException;
import com.fields4j.validators.core.Validator;
import com.google.common.base.Strings;

/**
 * Componente para representar un campo de un formulario. Un campo está compuesto por:
 * <ul>
 * <li>un label donde se muestra el nombre del campo(Edad, Nombre, Dirección, etc.)
 * <li>un label que muestra algún tipo de marca que le indica al usuario si el campo es requerido
 * o no.
 * <li>un componente principal donde generalmente se procesa la entrada de valores ({@link
 * JPasswordField}, {@link JSpinner}, etc.) aunque también puede ser un componente actuando
 * como un contenedor como en el caso de {@link TextField}.
 * </ul>
 * <p>
 * <strong>Nota:</strong> Las subclases tienen que invocar
 * {@link #fireValueChangeEvent(Object, Object)} con los valores adecuados cada vez que su
 * valor cambie.
 *
 * @param <MC> el tipo del componente principal del campo.
 * @param <VC> el tipo del componente usado para introducir el valor del campo.
 * @param <V>  el tipo de los valores que se pueden obtener de este campo.
 */
public abstract class Field<MC extends JComponent, VC extends JComponent, V> extends JPanel
    implements ValueChangeListener<V> {

  private static final ResourceBundle FIELD_BUNDLE = ResourceBundle.getBundle(
      "com/fields4j/resources/Field");

  public static FieldStyle ALL_FIELDS_STYLE = new DefaultFieldStyle();

  protected Border defaultBorder;

  private JLabel textLabel = new JLabel();
  protected JLabel requiredLabel = new JLabel();

  private MC mainComponent;
  private VC valueComponent;

  private FieldStyle fieldStyle = null;

  private String tooltipBackup = null;

  private Collection<Validator<V>> validators = new ArrayList<>();
  private boolean isRequired = false;

  private V initialValue;

  private boolean horizontal;
  private boolean labelsVisible;

  /**
   * Crea un nuevo campo usando {@code mainComponent} como componente principal, este es usado
   * también como {@code valueComponent}. Las subclases deben invocar el método {@link
   * #fireValueChangeEvent(Object, Object)} cada vez que el valor contenido en este componente
   * cambie.
   *
   * @param mainComponent componente principal del campo
   */
  protected Field(MC mainComponent) {
    this(mainComponent, (VC) mainComponent);
  }

  /**
   * Crea un nuevo campo usando {@code mainComponent} como componente principal y {@code
   * valueComponent}. Las subclases deben invocar el método
   * {@link #fireValueChangeEvent(Object, Object)} cada vez que el valor contenido en {@code
   * valueComponent} cambie.
   *
   * @param mainComponent  componente principal del campo
   * @param valueComponent componente usado para introducir el valor del campo
   */
  protected Field(MC mainComponent, VC valueComponent) {
    this.mainComponent = mainComponent;
    this.valueComponent = valueComponent;

    if (ALL_FIELDS_STYLE == null) {
      throw new IllegalArgumentException("FieldStyle can't be null");
    }

    initialValue = null;

    defaultBorder = this.valueComponent.getBorder();

    textLabel.setLabelFor(this.valueComponent);
    requiredLabel.setFocusable(false);

    horizontal = false;

    labelsVisible = true;
    textLabel.setVisible(true);
    requiredLabel.setVisible(true);

    String text = getClass().getSimpleName();
    int mnemonicIndex = getMnemonicIndex(text);

    setTextWithMnemonic(text, mnemonicIndex);
    this.valueComponent.setEnabled(true);

    setFieldStyle(ALL_FIELDS_STYLE);
    updateLayout();

    validateField();

    synchronized (getTreeLock()) {
      for (Component component : getComponents()) {
        component.setFocusable(false);
      }
    }

    this.mainComponent.setFocusable(false);
    this.valueComponent.setFocusable(true);

    addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        transferFocus();
      }
    });
  }

  /** Devuelve si el campo está vacío o no */
  public abstract boolean isEmpty();

  /** Devuelve el valor actual del campo */
  public abstract V getValue();

  /**
   * Cambia el valor actual del campo.
   *
   * @param value el nuevo valor
   */
  public abstract void setValue(V value);

  /**
   * Determina si los labels del campo están visibles o no
   *
   * @return {@code true} si están visibles, {@code false} en otro caso
   */
  public boolean isLabelsVisible() {
    return labelsVisible;
  }

  /**
   * Establece si los labels se muestran o no en la interfaz
   *
   * @param labelsVisible {@code true} si se muestran los labels, {@code false} en otro caso
   */
  public void setLabelsVisible(boolean labelsVisible) {
    this.labelsVisible = labelsVisible;
    textLabel.setVisible(labelsVisible);
    requiredLabel.setVisible(labelsVisible && isRequired());

    updateLayout();
  }

  @Override
  public boolean isFocusable() {
    return isEditable() && valueComponent.isFocusable();
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);

    textLabel.setEnabled(enabled);
    requiredLabel.setEnabled(enabled);
    mainComponent.setEnabled(enabled);

    if (enabled) {
      textLabel.setForeground(fieldStyle.getLabelForeground());
      requiredLabel.setForeground(fieldStyle.getRequiredMarkForeground());
    } else {
      textLabel.setForeground(fieldStyle.getDisabledFieldForeground());
      requiredLabel.setForeground(fieldStyle.getDisabledFieldForeground());
    }
  }

  @Override
  public void valueChanged(ValueChangeEvent<V> event) {
    validateField();
  }

  /**
   * Establece un valor inicial para el campo, establecer este valor sirve para saber si el valor
   * del campo ha cambiado usando el método {@link #hasChanged()}. Este método cambia el valor
   * actual del campo.
   *
   * @param value valor inicial para el campo
   */
  public void setInitialValue(V value) {
    initialValue = value;
    setValue(value);
  }

  public V getInitialValue(){
    return initialValue;
  }

  /**
   * Devuelve {@code true} si el valor de este campo puede ser modificado por el usuario. La
   * implementación por defecto deduce el resultado a partir de si el campo está habilitado, {@code
   * getValueComponent().isEnabled()}.
   */
  public boolean isEditable() {
    return valueComponent.isEnabled();
  }

  /**
   * Determina si el valor de este campo puede ser modificado por el usuario. La implementación por
   * defecto aplica esta propiedad habilitando/deshabilitando el campo, {@code
   * getValueComponent().setEnabled(editable)}.
   */
  public void setEditable(boolean editable) {
    valueComponent.setEnabled(editable);
    requiredLabel.setVisible(editable && isRequired());

    validateField();
  }

  /**
   * Determina si el valor del campo ha cambiado o no. El uso más común para este método es en
   * conjunción con {@link #setInitialValue(Object)} para tener un valor adecuado con el cual
   * comparar para saber si el valor del campo ha cambiado.
   *
   * @return {@code true} si el valor del campo ha cambiado
   */
  public boolean hasChanged() {
    if ((initialValue == null) && isEmpty()) {
      return false;
    }

    V value = getValue();

    if (value == null) {
      return initialValue == null;
    }

    return !value.equals(initialValue);
  }

  public boolean isRequired() {
    return isRequired;
  }

  public void setRequired(boolean required) {
    isRequired = required;

    if (isRequired) {
      requiredLabel.setIcon(fieldStyle.getRequiredMarkIcon());
      requiredLabel.setText(fieldStyle.getRequiredMarkText());
    } else {
      requiredLabel.setIcon(null);
      requiredLabel.setText("");
    }

    validateField();
  }

  /**
   * Incluye el validador en el proceso de verificación del campo. Sólo puede existir un validador
   * de cada tipo.
   */
  public void addValidator(Validator<V> validator) {
    Objects.requireNonNull(validator);
    validators.add(validator);

    validateField();
  }

  /**
   * Elimina el validador del tipo dado si existe alguno.
   *
   * @return {@code true} si se eliminó algún elemento.
   */
  public boolean removeValidator(Validator<V> validator) {
    Objects.requireNonNull(validator);
    boolean removed = validators.remove(validator);

    if (removed) {
      validateField();
    }

    return removed;
  }

  /** Elimina todos los validadores asociados a este campo. */
  public void removeValidators() {
    validators.clear();
    validateField();
  }

  /**
   * Agrega un {@code ValueChangeListener} que será notificado cada vez que el valor del campo
   * cambie.
   */
  public void addValueChangeListener(ValueChangeListener<V> listener) {
    listenerList.add(ValueChangeListener.class, listener);
  }

  /** Elimina el {@code ValueChangeListener} dado */
  public void removeValueChangeListener(ValueChangeListener<V> listener) {
    listenerList.remove(ValueChangeListener.class, listener);
  }

  public String getText() {
    return textLabel.getText();
  }

  public void setText(String text) {
    int mnemonicIndex = getMnemonicIndex(text);
    setTextWithMnemonic(text, mnemonicIndex);
  }

  /**
   * Realiza el proceso de validación pasando el valor actual del campo por todos los validadores
   * instalados. Devuelve {@code true} o {@code false} dependiendo de si el campo es válido o no.
   * <p>A diferencia de {@link #validateFieldSilently()} el resultado de este método se refleja
   * visualmente en el campo.
   */
  public final boolean validateField() {
    ValidationResult result = doValidateField();

    if (result.isValid()) {
      renderCorrectState();
    } else {
      renderErrorState(result.getCause().getMessage());
    }

    return result.isValid();
  }

  /**
   * Realiza el proceso de validación pasando el valor actual del campo por todos los validadores
   * instalados. Devuelve {@code true} o {@code false} dependiendo de si el campo es válido o no.
   * <p>A diferencia de {@link #validateField()} el resultado de este método <strong>no</strong> se
   * refleja visualmente en el campo.
   */
  public boolean validateFieldSilently() {
    return doValidateField().isValid();
  }

  /*
   * Devuelve el campo a su estado inicial, en el que no se ha realizado ninguna validación. La
   * implementación por defecto lo representa igual que en el estado de valiación correcta.
   */
  public void resetState() {
    renderCorrectState();
  }

  /** Devuelve {@code true} si y solo si los elementos del campo están alineados horizontalmente. */
  public boolean isHorizontal() {
    return horizontal;
  }

  /**
   * Establece la orientación de los elementos del campo.
   *
   * @param horizontal determina si los elementos del campo deben ser alineados horizontal o
   *                   verticalmente.
   */
  public void setHorizontal(boolean horizontal) {
    this.horizontal = horizontal;

    updateLayout();
  }

  public final VC getValueComponent() {
    return valueComponent;
  }

  public void renderCorrectState() {
    valueComponent.setBorder(defaultBorder);
    valueComponent.setToolTipText(Strings.emptyToNull(tooltipBackup));
  }

  public void renderErrorState(String message) {
    Border border = BorderFactory.createLineBorder(Color.RED);

    valueComponent.setBorder(border);
    valueComponent.setToolTipText(Strings.emptyToNull(message));
  }

  public FieldStyle getFieldStyle() {
    return fieldStyle;
  }

  public final void setFieldStyle(FieldStyle fieldStyle) {
    Objects.requireNonNull(fieldStyle);
    this.fieldStyle = fieldStyle;

    Color fieldBackground = fieldStyle.getFieldBackground();

    setBackground(fieldBackground);
    textLabel.setBackground(fieldBackground);
    requiredLabel.setBackground(fieldBackground);
    mainComponent.setBackground(fieldBackground);
    valueComponent.setBackground(fieldBackground);

    textLabel.setBackground(fieldStyle.getLabelBackground());
    textLabel.setFont(fieldStyle.getLabelFont());

    requiredLabel.setBackground(fieldStyle.getRequiredMarkBackground());
    requiredLabel.setFont(fieldStyle.getRequiredMarkFont());

    valueComponent.setFont(fieldStyle.getValueComponentFont());
    valueComponent.setBackground(fieldStyle.getValueComponentBackground());
    valueComponent.setForeground(fieldStyle.getValueComponentForeground());

    mainComponent.setBackground(fieldStyle.getMainComponentBackground());

    if (isRequired()) {
      requiredLabel.setText(fieldStyle.getRequiredMarkText());
      requiredLabel.setIcon(fieldStyle.getRequiredMarkIcon());
    }

    if (isEnabled()) {
      textLabel.setForeground(fieldStyle.getLabelForeground());
      requiredLabel.setForeground(fieldStyle.getRequiredMarkForeground());
    } else {
      textLabel.setForeground(fieldStyle.getDisabledFieldForeground());
      requiredLabel.setForeground(fieldStyle.getDisabledFieldForeground());
    }
  }

  /**
   * Devuelve el componente principal del campo, este puede ser el componente donde se introduce el
   * valor o puede ser un componente que actúa como un contenedor.
   */
  protected final MC getMainComponent() {
    return mainComponent;
  }

  /**
   * Lanza un {@link ValueChangeEvent} a todos los {@link ValueChangeListener} registrados. Las
   * subclases tienen que invocar este metodo cada vez que su valor cambie.
   */
  protected final void fireValueChangeEvent(V oldValue, V newValue) {
    ValueChangeEvent<V> event = new ValueChangeEvent<>(this, oldValue, newValue);

    // siempre notificar primero a esta misma clase para evitar valores de validacion incorrectos
    valueChanged(event);

    for (ValueChangeListener listener : listenerList.getListeners(ValueChangeListener.class)) {
      listener.valueChanged(event);
    }
  }

  protected final int getMnemonicIndex(String text) {
    int mnemonicCharIndex = -1;

    if (text.matches(".*&\\p{L}.*")) {
      mnemonicCharIndex = text.indexOf('&');
    }
    return mnemonicCharIndex;
  }

  private void setTextWithMnemonic(String text, int mnemonicIndex) {
    if (mnemonicIndex >= 0) {
      textLabel.setText(text.replaceFirst("&", ""));
      textLabel.setDisplayedMnemonic(text.charAt(mnemonicIndex));
      textLabel.setDisplayedMnemonicIndex(mnemonicIndex);
    } else {
      textLabel.setText(text);

      if (!text.isEmpty()) {
        textLabel.setDisplayedMnemonic(text.charAt(0));
      }
    }
  }

  private void updateLayout() {
    removeAll();

    JPanel textPanel = null;

    if (isLabelsVisible()) {
      textPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
      textPanel.setBackground(fieldStyle.getFieldBackground());
      textPanel.add(textLabel);

      textPanel.add(requiredLabel);
    }

    if (isHorizontal()) {
      setLayout(new BorderLayout(5, 0));
    } else {
      setLayout(new BorderLayout());
    }

    add(mainComponent, BorderLayout.CENTER);

    if (textPanel != null) {
      add(textPanel, isHorizontal() ? BorderLayout.WEST : BorderLayout.NORTH);
    }
  }

  /** Devuelve el componente usado para introducir el valor de este campo. */
  private ValidationResult doValidateField() {
    if (isEmpty()) {
      ValidationResult result;
      // si el campo esta vacio la validez de este depende de si es requerido o no

      if (isRequired()) {
        // el campo no puede ser valido si es obligatorio y no contiene informacion
        String reason = FIELD_BUNDLE.getString("requiredMessage");

        result = new ValidationResult(new ValidationException(reason));

      } else {
        // si el campo es opcional y esta vacio entonces no hay informacion que validar.
        result = new ValidationResult();
      }

      return result;
    }

    // el resto de las validaciones solo se llevan a cabo si el campo contiene alguna informacion
    try {
      V value = getValue();
      for (Validator<V> validator : validators) {
        validator.validate(value);
      }

      // no hay errores de validacion
      return new ValidationResult();

    }
    catch (ValidationException e) {
      return new ValidationResult(e);
    }
  }

  @Override
  public String getToolTipText() {
    return valueComponent.getToolTipText();
  }


  @Override
  public void setToolTipText(String text) {
    valueComponent.setToolTipText(text);
    tooltipBackup = text;
  }
}
