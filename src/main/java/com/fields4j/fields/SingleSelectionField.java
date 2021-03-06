package com.fields4j.fields;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.DefaultFormatterFactory;

import com.fields4j.FieldUtils;
import com.fields4j.core.Field;
import com.fields4j.fields.internal.CustomListDataIntelliHints;
import com.fields4j.fields.internal.CustomListDataIntelliHints.MatchMode;
import com.google.common.collect.ImmutableList;
import com.jidesoft.swing.JideSwingUtilities;
import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("unchecked")
public class SingleSelectionField<V> extends Field<JPanel, JFormattedTextField, V> {

  public static String globalTransientRemovalWarningTitle = null;
  public static String globalTransientRemovalWarningFormat = null;

  private static final ResourceBundle bundle = ResourceBundle.getBundle(
      "com/fields4j/resources/SingleSelectionField");

  private boolean blankItemPresent;
  private V blankItem = null;
  private String blankItemText = bundle.getString("blankChoice.text");

  private V transientItem = null;

  private JButton addOptionButton;
  private boolean addOptionVisible;
  private ActionListener addOptionListener = null;

  private PropertyChangeListener changeListener;

  private CustomDefaultFormatter formatter;

  private CustomListDataIntelliHints<V> intelliHints = new CustomListDataIntelliHints<>(
      getValueComponent(), new ArrayList<>());

  private JButton arrowButton = new BasicArrowButton(SwingConstants.SOUTH);

  private String transientRemovalWarningTitle = null;
  private String transientRemovalWarningFormat = null;

  private Comparator<V> itemsComparator = null;

  /** Crea un nuevo {@code SingleSelectionField} sin la opción de agregar un elemento. */
  public SingleSelectionField() {
    this(false);
  }

  /**
   * Crea un nuevo {@code SingleSelectionField}. Si {@code addOptionVisible} es {@code true} se
   * muestra un botón que permite insertar un nuevo elemento; la acción que se realizará cuando se
   * presione este botón puede establecerse usando {@link #setAddOptionListener(ActionListener)}.
   *
   * @param addOptionVisible si es {@code true} se muestra un botón que permite insertar un nuevo
   *                         elemento.
   */
  public SingleSelectionField(boolean addOptionVisible) {
    super(new JPanel(), new JFormattedTextField());
    getValueComponent().setFormatterFactory(
        new DefaultFormatterFactory(new CustomDefaultFormatter()));

    setText(getClass().getSimpleName());

    intelliHints.setCaseSensitive(false);
    intelliHints.setFollowCaret(false);
    intelliHints.setMatchMode(MatchMode.CONTAINS);

    intelliHints.setFont(getFieldStyle().getLabelFont());
    intelliHints.setForegroundColor(getFieldStyle().getLabelForeground());

    JFormattedTextField valueComponent = getValueComponent();

    formatter = (CustomDefaultFormatter) valueComponent.getFormatter();

    changeListener = evt -> {
      if ("value".equalsIgnoreCase(evt.getPropertyName())) {

        if ((transientItem != null) && Objects.equals(evt.getOldValue(), transientItem)) {
          // se deselecciono el transientItem

          String warningTitle = getTransientRemovalWarningTitle();
          String warningFormat = getTransientRemovalWarningFormat();

          String message = String.format(warningFormat, transientItem);

          Window parentWindow = SwingUtilities.getWindowAncestor(this);
          int choice = JOptionPane.showConfirmDialog(parentWindow, message, warningTitle,
                                                     JOptionPane.OK_CANCEL_OPTION,
                                                     JOptionPane.QUESTION_MESSAGE);

          if ((choice == JOptionPane.CANCEL_OPTION) || (choice == JOptionPane.CLOSED_OPTION)) {
            // desactivar temporalmente el PropertyChangeListener para no publicar los cambios q se van a realizar
            valueComponent.removePropertyChangeListener(changeListener);

            // volver a seleccionar el transientItem ya que el usuario decidio no cambiar la seleccion
            setValue(transientItem);

            // volver a activar el PropertyChangeListener
            valueComponent.addPropertyChangeListener(changeListener);

            // no se propaga el cambio de valor
            return;
          }
        }

        removeTransientItemIfNeeded();
        fireValueChangeEvent(null, getValue());
      }
    };

    valueComponent.addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        if (!e.isTemporary()) {
          SwingUtilities.invokeLater(valueComponent::selectAll);
        }
      }
    });

    valueComponent.addPropertyChangeListener(changeListener);

    String iconPath = "/com/fields4j/resources/images/agregar-valor.png";
    addOptionButton = new JButton(FieldUtils.getIcon(iconPath));

    addOptionButton.setBackground(getFieldStyle().getMainComponentBackground());
    addOptionButton.setMargin(new Insets(5, 5, 5, 5));

    getMainComponent().setFocusable(false);

    setAddOptionVisible(addOptionVisible);
    setBlankItemPresent(true);
  }

  @Override
  public Dimension getPreferredSize() {
    Comparator<V> lengthComparator = Comparator.comparing(v -> String.valueOf(v).length());
    List<V> completionList = intelliHints.getCompletionList();

    if (completionList.isEmpty()) {
      return new JLabel(getClass().getSimpleName()).getPreferredSize();
    }

    String largest = String.valueOf(Collections.max(completionList, lengthComparator));

    Component component = new JLabel(StringUtils.rightPad(largest, largest.length() + 10));
    return component.getPreferredSize();
  }

  public String getTransientRemovalWarningTitle() {
    if (transientRemovalWarningTitle == null) {
      if (globalTransientRemovalWarningFormat == null) {
        return bundle.getString("transientRemovalWarning.title");
      }

      return globalTransientRemovalWarningTitle;
    }
    return transientRemovalWarningTitle;
  }

  public void setTransientRemovalWarningTitle(String transientRemovalWarningTitle) {
    this.transientRemovalWarningTitle = transientRemovalWarningTitle;
  }

  public String getTransientRemovalWarningFormat() {
    if (transientRemovalWarningFormat == null) {
      if (globalTransientRemovalWarningFormat == null) {
        return bundle.getString("transientRemovalWarning.format");
      }

      return globalTransientRemovalWarningFormat;
    }
    return transientRemovalWarningFormat;
  }

  public void setTransientRemovalWarningFormat(String transientRemovalWarningFormat) {
    this.transientRemovalWarningFormat = transientRemovalWarningFormat;
  }

  public String getBlankItemText() {
    return blankItemText;
  }

  public void setBlankItemText(String blankItemText) {
    if (!blankItemText.equals(this.blankItemText)) {
      this.blankItemText = blankItemText;

      // recargar para q se actualice el texto del item vacio
      setBlankItemPresent(blankItemPresent);
      setItems(getItems());
    }
  }

  /**
   * Devuelve {@code true} solo si se está mostrando la opción de agregar nuevos elementos al campo.
   */
  public boolean isAddOptionVisible() {
    return addOptionVisible;
  }

  /**
   * Determina si se debe mostrar o no la opción de agregar nuevos elementos al campo. El
   * comportamiento de esta opción se puede establecer usando {@link
   * #setAddOptionListener(ActionListener)}.
   */
  public final void setAddOptionVisible(boolean visible) {
    JPanel mainComponent = getMainComponent();
    Color background = getFieldStyle().getMainComponentBackground();

    mainComponent.removeAll();
    mainComponent.setLayout(new BorderLayout(visible ? 2 : 0, 0));

    arrowButton.setBackground(background);

    arrowButton.addActionListener(e -> {
      getValueComponent().requestFocusInWindow();
      getValueComponent().selectAll();
      intelliHints.showHints(true);
    });

    JPanel tempPanel = new JPanel(new BorderLayout());
    tempPanel.setBackground(background);
    tempPanel.add(getValueComponent(), BorderLayout.CENTER);
    tempPanel.add(arrowButton, BorderLayout.LINE_END);

    mainComponent.add(tempPanel, BorderLayout.CENTER);

    addOptionButton.setBackground(background);
    addOptionButton.setVisible(visible && isEditable());
    addOptionButton.setFocusable(visible && isEditable());

    if (visible) {
      mainComponent.add(addOptionButton, BorderLayout.LINE_END);
    }

    addOptionVisible = visible;
  }

  /**
   * Devuelve si el elemento vacío se mostrará o no cuando no haya ninguna opción seleccionada.
   * <p>Se puede usar el método {@link #isBlankItemSelected()} para diferenciar la opción nula de
   * una selección válida.
   */
  public boolean isBlankItemPresent() {
    return blankItemPresent;
  }

  /**
   * Determina si se debe mostrar un elemento que represente la selección nula cuando no haya ningún
   * elemento válido seleccionado.
   * <p>Se puede usar el método {@link #isBlankItemSelected()} para diferenciar la opción nula de
   * una selección válida.
   */
  public final void setBlankItemPresent(boolean blankItemPresent) {
    this.blankItemPresent = blankItemPresent;

    if (blankItemPresent) {
      blankItem = (V) blankItemText;
    } else {
      blankItem = null;
    }
  }

  /** Devuelve {@code true o false} si el elemento seleccionado actualmente es la opción nula. */
  public boolean isBlankItemSelected() {
    return isItemSelected(blankItem);
  }

  @Override
  public boolean isEmpty() {
    if (intelliHints == null) {
      return true;
    }

    List<V> completionList = intelliHints.getCompletionList();
    return completionList.isEmpty() || (blankItemPresent && isBlankItemSelected());
  }

  @Override
  public V getValue() {
    Object value = getValueComponent().getValue();
    return Objects.equals(value, blankItem) ? null : (V) value;
  }

  /**
   * Cambia el item seleccionado en el combo box, si el valor indicado no es uno de los valores de
   * este campo el item es agregado en un estado "efímero" ya que se insertará y seleccionará en el
   * campo pero una vez que otro item sea seleccionado este valor será eliminado del campo.
   *
   * @param value el nuevo valor a seleccionar
   */
  @Override
  public void setValue(V value) {
    if (value == null) {
      resetState();
      return;
    }

    JFormattedTextField valueComponent = getValueComponent();
    List<V> completionList = intelliHints.getCompletionList();

    boolean isPresent = internalContains(completionList, value);

    if (isPresent) {
      valueComponent.setValue(value);
    } else {
      transientItem = value;

      Collection<V> newCompletionList = new ArrayList<>(completionList);
      newCompletionList.add(value);
      internalSetItems(newCompletionList);

      setValue(transientItem);
    }
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);

    JideSwingUtilities.setEnabledRecursively(getValueComponent(), enabled);

    if (arrowButton != null) {
      arrowButton.setEnabled(enabled);
    }
  }

  @Override
  public void setEditable(boolean editable) {
    getValueComponent().setEnabled(editable);
    getValueComponent().setFocusable(editable);

    requiredLabel.setVisible(editable && isRequired());

    if (addOptionButton != null) {
      addOptionButton.setVisible(editable && addOptionVisible);
    }

    if (arrowButton != null) {
      arrowButton.setVisible(editable);
    }
  }

  @Override
  public void resetState() {
    addBlankIfNeeded();

    if (blankItemPresent) {
      setInitialValue(blankItem);
    }
    super.resetState();
  }

  public void setItemsComparator(Comparator<V> itemsComparator) {
    this.itemsComparator = itemsComparator;
  }

  /**
   * Establece un {@link ActionListener} que será invocado cuando se presione el botón de agregar un
   * nuevo elemento. Si es {@code null} se elimina cualquier {@code ActionListener} establecido
   * anteriormente.
   */
  public void setAddOptionListener(ActionListener listener) {
    if (addOptionListener != null) {
      addOptionButton.removeActionListener(addOptionListener);
    }

    addOptionListener = listener;

    if (addOptionListener != null) {
      addOptionButton.addActionListener(event -> {
        List<V> itemsBefore = getItems();

        addOptionListener.actionPerformed(event);

        List<V> itemsAfter = getItems();

        // encontrar el nuevo elemento y establecerlo como el valor actual
        Optional<V> newItem = getLastNew(itemsBefore, itemsAfter);
        newItem.ifPresent(this::setValue);

        // devolver el foco a la lista de opciones
        getValueComponent().requestFocusInWindow();
      });
    }
  }

  /** Devuelve todos los valores contenidos en el campo, sin incluir el elemento en blanco. */
  public List<V> getItems() {
    Collection elements = new ArrayList();

    for (V item : intelliHints.getCompletionList()) {
      if (blankItemPresent && !blankItem.equals(item)) {
        elements.add(item);
      }
    }

    return ImmutableList.copyOf(elements);
  }

  /**
   * Reemplaza lo elementos presentes en el campo con los provistos como argumento. Si {@link
   * #isBlankItemPresent()} es {@code true} se agrega un elemento extra que será considerado como la
   * selección nula o vacía del componente; si es {@code false} entonces uno de los nuevos elementos
   * quedará seleccionado.
   *
   * @param items los nuevos elementos del campo
   */
  public void setItems(List<V> items) {
    getValueComponent().removePropertyChangeListener(changeListener);

    internalSetItems(items);

    if (blankItemPresent) {
      setValue(blankItem);
    } else {
      if (!items.isEmpty()) {
        setValue(items.get(0));
      }
    }

    getValueComponent().addPropertyChangeListener(changeListener);
    resetState();
  }

  /** Devuelve {@code true} si el elemento dado es uno de los elementos seleccionables del campo. */
  public boolean contains(V item) {
    return internalContains(getItems(), item);
  }

  private boolean internalContains(Collection<V> collection, V value) {
    if (itemsComparator == null) {
      return collection.contains(value);
    }

    return collection.stream().anyMatch(v -> {
      if ((v == blankItem) || (value == blankItem)) {
        return v == value;
      }
      return itemsComparator.compare(v, value) == 0;
    });
  }

  private void internalSetItems(Collection<V> items) {
    List<V> newElements = new ArrayList<>();

    if (blankItemPresent && !items.contains(blankItem)) {
      newElements.add(blankItem);
    }
    newElements.addAll(items);

    formatter.setAcceptedValues(newElements);
    intelliHints.setCompletionList(newElements);
  }

  private boolean isItemSelected(V item) {
    return Objects.equals(getValueComponent().getValue(), item);
  }

  private void removeTransientItemIfNeeded() {
    if ((transientItem != null) && !isItemSelected(transientItem)) {
      List<V> completionList = intelliHints.getCompletionList();

      if (completionList.remove(transientItem)) {
        internalSetItems(completionList);
      }
    }
  }

  private void addBlankIfNeeded() {
    List<V> completionList = intelliHints.getCompletionList();

    if (blankItemPresent && !completionList.contains(blankItem)) {
      Collection<V> newCompletionList = new ArrayList<>(completionList.size() + 1);

      newCompletionList.add(blankItem);
      newCompletionList.addAll(completionList);

      internalSetItems(newCompletionList);
    }
  }

  private Optional<V> getLastNew(Collection<V> before, Collection<V> after) {
    Optional<V> lastNew = Optional.empty();

    for (V element : after) {
      if (!internalContains(before, element)) {
        lastNew = Optional.of(element);
      }
    }

    return lastNew;
  }

  private class CustomDefaultFormatter extends AbstractFormatter {

    private Collection acceptedValues = new ArrayList<>();

    @Override
    public Object stringToValue(String text) throws ParseException {
      for (Object acceptedValue : acceptedValues) {
        if (String.valueOf(acceptedValue).equals(text)) {
          return acceptedValue;
        }
      }

      throw new ParseException("", 0);
    }

    @Override
    public String valueToString(Object value) throws ParseException {
      if (internalContains(acceptedValues, (V) value)) {
        return (value == null) ? "" : value.toString();
      }

      throw new ParseException(String.format("error[%s]", value), 0);
    }

    void setAcceptedValues(List acceptedValues) {
      this.acceptedValues = new ArrayList<>(acceptedValues);
    }
  }
}

