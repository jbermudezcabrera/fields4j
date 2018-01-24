package com.fields4j.fields;

import java.awt.event.ItemEvent;
import javax.swing.JCheckBox;

import com.fields4j.core.Field;
import com.fields4j.core.FieldStyle;

public class BooleanField extends Field<JCheckBox, JCheckBox, Boolean> {

  public BooleanField() {
    super(new JCheckBox());
    setInitialValue(false);

    JCheckBox valueComponent = getValueComponent();

    valueComponent.addItemListener(event -> {
      boolean oldV;
      boolean newV;

      if (event.getStateChange() == ItemEvent.SELECTED) {
        oldV = false;
        newV = true;
      } else {
        oldV = true;
        newV = false;
      }

      fireValueChangeEvent(oldV, newV);
    });

    super.setHorizontal(true);
    super.setLabelsVisible(false);

    FieldStyle fieldStyle = getFieldStyle();

    valueComponent.setFont(fieldStyle.getLabelFont());
    valueComponent.setForeground(fieldStyle.getLabelForeground());
    valueComponent.setBackground(fieldStyle.getLabelBackground());
  }

  @Override
  public boolean isEmpty() {
    // un checkbox nunca esta vacio
    return false;
  }

  @Override
  public Boolean getValue() {
    return getValueComponent().isSelected();
  }

  @Override
  public void setValue(Boolean value) {
    getValueComponent().setSelected(value);
  }

  @Override
  public boolean isLabelsVisible() {
    return false;
  }

  @Override
  public void setLabelsVisible(boolean labelsVisible) {
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);

    JCheckBox valueComponent = getValueComponent();

    if (enabled) {
      valueComponent.setForeground(getFieldStyle().getLabelForeground());
    } else {
      valueComponent.setForeground(getFieldStyle().getDisabledFieldForeground());
    }
  }

  @Override
  public boolean isRequired() {
    return false;
  }

  @Override
  public void setRequired(boolean required) {
  }

  @Override
  public String getText() {
    return getValueComponent().getText();
  }

  @Override
  public void setText(String text) {
    JCheckBox checkBox = getValueComponent();
    int mnemonicIndex = getMnemonicIndex(text);

    if (mnemonicIndex >= 0) {
      checkBox.setText(text.replaceFirst("&", ""));
      checkBox.setDisplayedMnemonicIndex(mnemonicIndex);
    } else {
      checkBox.setText(text);

      if (!text.isEmpty()) {
        checkBox.setDisplayedMnemonicIndex(0);
      }
    }
  }

  @Override
  public boolean isHorizontal() {
    // la orientacion vertical de un checkbox es poco atractiva visualmente
    return true;
  }

  @Override
  public void setHorizontal(boolean horizontal) {
  }
}
