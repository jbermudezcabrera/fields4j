package com.fields4j.fields;

import com.fields4j.FieldUtils;
import com.fields4j.core.Field;
import com.google.common.collect.ImmutableList;
import com.jidesoft.hints.ListDataIntelliHints;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.*;
import java.util.List;

@SuppressWarnings("unchecked")
public class SingleSelectionField <V> extends Field<JPanel, JFormattedTextField, V> {
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
      "com/fields4j/resources/SingleSelectionField");

  private boolean blankItemPresent;
  private V      blankItem     = null;
  private String blankItemText = BUNDLE.getString("blankChoice.text");

  private V transientItem = null;

  private JButton addOptionButton;
  private boolean addOptionVisible;
  private ActionListener addOptionListener = null;

  private PropertyChangeListener changeListener;

  private CustomDefaultFormatter formatter;

  private CustomListDataIntelliHints<V> intelliHints = new CustomListDataIntelliHints<>(getValueComponent(), new ArrayList<>());

  private JButton arrowButton = new BasicArrowButton(SwingConstants.SOUTH);

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
    super(new JPanel(), new JFormattedTextField(new CustomDefaultFormatter()));
    setText(getClass().getSimpleName());

    intelliHints.setCaseSensitive(false);
    intelliHints.setFollowCaret(false);
    intelliHints.setMatchMode(CustomListDataIntelliHints.MatchMode.CONTAINS);

    intelliHints.setFont(getFieldStyle().getLabelFont());
    intelliHints.setForegroundColor(getFieldStyle().getLabelForeground());

    JFormattedTextField valueComponent = getValueComponent();

    formatter = (CustomDefaultFormatter) valueComponent.getFormatter();

    changeListener = evt -> {
      if ("value".equalsIgnoreCase(evt.getPropertyName())) {
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
    List<V>       completionList   = intelliHints.getCompletionList();

    if (completionList.isEmpty()) {
      return new JLabel(getClass().getSimpleName()).getPreferredSize();
    }

    String largest = String.valueOf(Collections.max(completionList, lengthComparator));

    Component component = new JLabel(StringUtils.rightPad(largest, largest.length() + 10));
    return component.getPreferredSize();
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
    Color  background    = getFieldStyle().getMainComponentBackground();

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
    tempPanel.add(arrowButton, BorderLayout.EAST);

    mainComponent.add(tempPanel, BorderLayout.CENTER);

    addOptionButton.setBackground(background);
    addOptionButton.setVisible(visible && isEditable());
    addOptionButton.setFocusable(visible && isEditable());

    if (visible) {
      mainComponent.add(addOptionButton, BorderLayout.EAST);
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
    }
    else {
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
    JFormattedTextField valueComponent = getValueComponent();
    List<V>             completionList = intelliHints.getCompletionList();

    if (completionList.contains(value)) {
      valueComponent.setValue(value);
    }
    else {
      transientItem = value;

      List<V> newCompletionList = new ArrayList<>(completionList);
      newCompletionList.add(value);
      internalSetItems(newCompletionList);

      setValue(transientItem);
    }
  }

  @Override
  public void setEditable(boolean editable) {
    getValueComponent().setEnabled(editable);
    getValueComponent().setFocusable(editable);

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
        newItem.ifPresent(SingleSelectionField.this::setValue);

        // devolver el foco a la lista de opciones
        getValueComponent().requestFocusInWindow();
      });
    }
  }

  /** Devuelve todos los valores contenidos en el campo, sin incluir el elemento en blanco. */
  public List<V> getItems() {
    List elements = new ArrayList();

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
    }
    else {
      if (!items.isEmpty()) {
        setValue(items.get(0));
      }
    }

    getValueComponent().addPropertyChangeListener(changeListener);
    resetState();
  }

  /** Devuelve {@code true} si el elemento dado es uno de los elementos seleccionables del campo. */
  public boolean contains(V item) {
    return getItems().contains(item);
  }

  private void internalSetItems(List<V> items) {
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
      List<V> newCompletionList = new ArrayList<>(completionList.size() + 1);

      newCompletionList.add(blankItem);
      newCompletionList.addAll(completionList);

      internalSetItems(newCompletionList);
    }
  }

  private Optional<V> getLastNew(Collection<V> before, Collection<V> after) {
    Optional<V> lastNew = Optional.empty();

    for (V element : after) {
      if (!before.contains(element)) {
        lastNew = Optional.of(element);
      }
    }

    return lastNew;
  }

  private static class CustomDefaultFormatter extends JFormattedTextField.AbstractFormatter {
    private List acceptedValues = new ArrayList<>();

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
      if (acceptedValues.contains(value)) {
        return (value == null) ? "" : value.toString();
      }

      throw new ParseException("", 0);
    }

    void setAcceptedValues(List acceptedValues) {
      this.acceptedValues = new ArrayList<>(acceptedValues);
    }
  }
}

class CustomListDataIntelliHints <E> extends ListDataIntelliHints<E> {
  private DefaultListCellRenderer listCellRenderer;

  private MatchMode matchMode = MatchMode.STARTS_WTIH;

  private Font  font            = null;
  private Color foregroundColor = null;

  CustomListDataIntelliHints(JTextComponent comp, List<E> completionList) {
    super(comp, completionList);

    listCellRenderer = new DefaultListCellRenderer() {
      @Override
      public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                    boolean isSelected, boolean cellHasFocus) {

        Component component = super.getListCellRendererComponent(list, value, index, isSelected,
                                                                 cellHasFocus);

        Object context  = getContext();
        String valueStr = "";

        if ((context != null) && (value != null)) {
          valueStr = value.toString();

          Pair<Integer, Integer> range = getMatchRange(valueStr, context.toString());
          Integer                start = range.getLeft();
          Integer                end   = range.getRight();

          if ((start >= 0) && (end >= 0)) {
            String beforeMatch = valueStr.substring(0, start);
            String afterMatch  = valueStr.substring(end);

            String match = valueStr.substring(start, end);

            int R = foregroundColor.getRed();
            int G = foregroundColor.getGreen();
            int B = foregroundColor.getBlue();

            String format = "<html>%s<strong style='color: rgb(%d,%d,%d)'>%s</strong>%s</html>";
            valueStr = String.format(format, beforeMatch, R, G, B, match, afterMatch);
          }
        }

        ((JLabel) component).setText(valueStr);
        component.setFont(font);
        return component;
      }
    };
  }

  @Override
  public JComponent createHintsComponent() {
    JComponent hintsComponent = super.createHintsComponent();

    getList().setCellRenderer(listCellRenderer);

    return hintsComponent;
  }

  @Override
  public boolean updateHints(Object context) {
    if (context != null) {
      String         contextStr    = context.toString();
      JTextComponent textComponent = getTextComponent();

      boolean allTextSelected = contextStr.equals(textComponent.getSelectedText());

      if (allTextSelected) {
        setListData(getCompletionList().toArray());
        return true;
      }
    }

    return super.updateHints(context);
  }

  @Override
  protected boolean compare(Object context, E element) {
    String listEntry  = (element == null) ? "" : element.toString();
    String contextStr = context.toString();

    Pair<Integer, Integer> match = getMatchRange(listEntry, contextStr);

    switch (matchMode) {
      case STARTS_WTIH:
        return match.getLeft() == 0;

      case CONTAINS:
        return match.getLeft() >= 0;
    }

    return false;
  }

  @Override
  public void acceptHint(Object selected) {
    super.acceptHint(selected);

    if (getTextComponent() instanceof JFormattedTextField) {
      ((JFormattedTextField) getTextComponent()).setValue(selected);
    }

    getTextComponent().selectAll();
  }

  void setForegroundColor(Color foregroundColor) {
    this.foregroundColor = foregroundColor;
  }

  void setFont(Font font) {
    this.font = font.deriveFont(Font.PLAIN);
  }

  MatchMode getMatchMode() {
    return matchMode;
  }

  void setMatchMode(MatchMode matchMode) {
    this.matchMode = matchMode;
  }

  private Pair<Integer, Integer> getMatchRange(String str, String pattern) {
    int start;

    if (isCaseSensitive()) {
      start = str.indexOf(pattern);
    }
    else {
      start = StringUtils.indexOfIgnoreCase(str, pattern);
    }

    return Pair.of(start, start + pattern.length());
  }

  enum MatchMode {
    STARTS_WTIH, CONTAINS
  }
}
