package com.fields4j.fields;

import java.awt.BorderLayout;
import java.io.File;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import com.fields4j.FieldUtils;
import com.fields4j.core.Field;
import com.jidesoft.swing.JideBoxLayout;

public class DirectorySelectionField extends Field<JPanel, StringField, String> {

  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
      "com/fields4j/resources/DirectorySelectionField");

  private JButton browseButton;

  private boolean editable;

  public DirectorySelectionField() {
    super(new JPanel(new BorderLayout(5, 0)), new StringField());

    browseButton = createBrowseButton();

    StringField valueComponent = getValueComponent();
    valueComponent.setLabelsVisible(false);
    valueComponent.setEditable(false);

    valueComponent.addValueChangeListener(
        event -> fireValueChangeEvent(event.getOldValue(), event.getNewValue()));

    JPanel mainComponent = getMainComponent();

    mainComponent.add(getValueComponent(), BorderLayout.CENTER);
    mainComponent.setBackground(getFieldStyle().getMainComponentBackground());

    mainComponent.add(createButtonsPanel(), BorderLayout.EAST);
  }

  @Override
  public boolean isEmpty() {
    return getValueComponent().isEmpty();
  }

  @Override
  public String getValue() {
    return getValueComponent().getValue();
  }

  @Override
  public void setValue(String value) {
    getValueComponent().setValue(value);
  }

  @Override
  public boolean isEditable() {
    return editable;
  }

  @Override
  public void setEditable(boolean editable) {
    this.editable = editable;

    if (browseButton != null) {
      browseButton.setEnabled(editable);
    }
  }

  /**
   * Devuelve el texto mostrado como sugerencia cuando el componente está fuera de foco y no
   * contenga ningún directorio.
   */
  public String getPlaceholderText() {
    return getValueComponent().getPlaceholderText();
  }

  /**
   * Establece un texto para mostrar con un formato de sugerencia cuando el componente esté fuera de
   * foco y no contenga ningún directorio.
   */
  public void setPlaceholderText(String placeholderText) {
    getValueComponent().setPlaceholderText(placeholderText);
  }

  private JPanel createButtonsPanel() {
    JPanel panel = new JPanel();
    setBackground(getFieldStyle().getMainComponentBackground());
    panel.setLayout(new JideBoxLayout(panel, JideBoxLayout.X_AXIS, 5));
    panel.add(browseButton, JideBoxLayout.FIX);

    return panel;
  }

  private JButton createBrowseButton() {
    String iconPath = "/com/fields4j/resources/images/buscar.png";
    String rolloverIconPath = "/com/fields4j/resources/images/buscar-rollover.png";

    JButton button = new JButton();
    button.setIcon(FieldUtils.getIcon(iconPath));
    button.setRolloverIcon(FieldUtils.getIcon(rolloverIconPath));
    button.setToolTipText(BUNDLE.getString("browseButton.tooltip"));

    button.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setAcceptAllFileFilterUsed(false);

      fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

      int userAction = fileChooser.showOpenDialog(SwingUtilities.getWindowAncestor(this));

      if (userAction == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();

        if (selectedFile != null) {
          setValue(selectedFile.getAbsolutePath());
        }
      }
    });

    return button;
  }
}
