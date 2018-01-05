package com.fields4j.fields;

import com.fields4j.core.Field;

import java.time.Month;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonthField
    extends Field<SingleSelectionField<String>, SingleSelectionField<String>, Month> {

  public MonthField() {
    super(new SingleSelectionField<>());

    List<String> months = new ArrayList<>();

    for (Month month : Month.values()) {
      months.add(monthToString(month));
    }

    getValueComponent().setItems(months);
    getValueComponent().addValueChangeListener(event -> fireValueChangeEvent(null, null));
  }

  @Override
  public boolean isEmpty() {
    return getValueComponent().isEmpty();
  }

  @Override
  public Month getValue() {
    return stringToMonth(getValueComponent().getValue());
  }

  @Override
  public void setValue(Month value) {
    getValueComponent().setValue(monthToString(value));
  }

  @Override
  public void setLabelsVisible(boolean labelsVisible) {
    super.setLabelsVisible(labelsVisible);
    getValueComponent().setLabelsVisible(labelsVisible);
  }

  @Override
  public void setHorizontal(boolean horizontal) {
    super.setHorizontal(horizontal);
    getValueComponent().setHorizontal(horizontal);
  }

  @Override
  public void setEditable(boolean editable) {
    super.setEditable(editable);
    getValueComponent().setEditable(editable);
  }

  @Override
  public void resetState() {
    super.resetState();
    getValueComponent().resetState();
  }

  public boolean isMonthOptional() {
    return getValueComponent().isBlankItemPresent();
  }

  public void setMonthOptional(boolean optional) {
    getValueComponent().setBlankItemPresent(optional);
  }

  private String monthToString(Month month) {
    return month.getDisplayName(TextStyle.FULL, Locale.getDefault());
  }

  private Month stringToMonth(String displayName) {
    for (Month month : Month.values()) {
      if (monthToString(month).equalsIgnoreCase(displayName)) {
        return month;
      }
    }

    return null;
  }
}
