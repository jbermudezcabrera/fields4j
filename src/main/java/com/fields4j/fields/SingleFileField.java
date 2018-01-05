package com.fields4j.fields;

import com.fields4j.FieldUtils;
import com.fields4j.core.Field;
import com.google.common.base.Strings;
import com.jidesoft.swing.JideBoxLayout;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SingleFileField extends Field<JPanel, StringField, String> {

  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
      "com/fields4j/resources/SingleFileField");

  private JButton browseButton;
  private JButton viewButton;
  private JButton deleteButton;

  private boolean editable;

  private ActionListener viewFileActionListener = null;

  private List<FileFilter> fileFilters        = new ArrayList<>();
  private boolean          useAcceptAllFilter = true;

  public SingleFileField() {
    super(new JPanel(new BorderLayout(5, 0)), new StringField());

    browseButton = createBrowseButton();
    viewButton = createViewButton();
    deleteButton = createDeleteButton();

    StringField valueComponent = getValueComponent();
    valueComponent.setLabelsVisible(false);
    valueComponent.setEditable(false);

    valueComponent.addValueChangeListener(event -> {
      String newValue = Strings.nullToEmpty(event.getNewValue());

      viewButton.setEnabled(!newValue.isEmpty());
      deleteButton.setEnabled(!newValue.isEmpty());

      fireValueChangeEvent(event.getOldValue(), event.getNewValue());
    });

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

    if (deleteButton != null) {
      deleteButton.setEnabled(editable);
    }
  }

  public boolean isViewButtonVisible() {
    return viewButton.isVisible();
  }

  public void setViewButtonVisible(boolean visible) {
    viewButton.setVisible(visible);
  }

  public boolean isDeleteButtonVisible() {
    return deleteButton.isVisible();
  }

  public void setDeleteButtonVisible(boolean visible) {
    deleteButton.setVisible(visible);
  }

  /**
   * Devuelve el texto mostrado como sugerencia cuando el componente está fuera de foco y no
   * contenga ningún archivo.
   */
  public String getPlaceholderText() {
    return getValueComponent().getPlaceholderText();
  }

  /**
   * Establece un texto para mostrar con un formato de sugerencia cuando el componente esté fuera de
   * foco y no contenga ningún archivo.
   */
  public void setPlaceholderText(String placeholderText) {
    getValueComponent().setPlaceholderText(placeholderText);
  }

  public ActionListener getViewFileActionListener() {
    return viewFileActionListener;
  }

  public void setViewFileActionListener(ActionListener viewFileActionListener) {
    this.viewFileActionListener = viewFileActionListener;
  }

  public List<FileFilter> getFileFilters() {
    return fileFilters;
  }

  public boolean getUseAcceptAllFilter() {
    return useAcceptAllFilter;
  }

  public void setUseAcceptAllFilter(boolean useAcceptAllFilter) {
    this.useAcceptAllFilter = useAcceptAllFilter;
  }

  private JButton createDeleteButton() {
    String iconPath         = "/com/fields4j/resources/images/eliminar.png";
    String rolloverIconPath = "/com/fields4j/resources/images/eliminar-rollover.png";

    JButton button = new JButton(FieldUtils.getIcon(iconPath));
    button.setRolloverIcon(FieldUtils.getIcon(rolloverIconPath));

    button.setEnabled(false);
    button.addActionListener(event -> setValue(""));

    return button;
  }

  private JPanel createButtonsPanel() {
    JPanel panel = new JPanel();
    setBackground(getFieldStyle().getMainComponentBackground());
    panel.setLayout(new JideBoxLayout(panel, JideBoxLayout.X_AXIS, 5));
    panel.add(browseButton, JideBoxLayout.FIX);
    panel.add(deleteButton, JideBoxLayout.FIX);
    panel.add(viewButton, JideBoxLayout.FIX);

    return panel;
  }

  private JButton createViewButton() {
    String iconPath         = "/com/fields4j/resources/images/view.png";
    String rolloverIconPath = "/com/fields4j/resources/images/view-rollover.png";

    JButton button = new JButton(FieldUtils.getIcon(iconPath));
    button.setRolloverIcon(FieldUtils.getIcon(rolloverIconPath));
    button.setToolTipText(BUNDLE.getString("viewTooltip"));

    button.setEnabled(false);

    button.addActionListener(event -> {
      if (viewFileActionListener != null) {
        viewFileActionListener.actionPerformed(event);
      }
    });

    return button;
  }

  private JButton createBrowseButton() {
    JButton button = new JButton(BUNDLE.getString("browseButton.text"));

    button.addActionListener(e -> {
      JFileChooser fileChooser = new JFileChooser();

      for (FileFilter fileFilter : fileFilters) {
        fileChooser.addChoosableFileFilter(fileFilter);
      }
      fileChooser.setAcceptAllFileFilterUsed(useAcceptAllFilter);

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
