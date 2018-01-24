package com.fields4j.fields;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class YearField extends SingleSelectionField<Integer> {

  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
      "com/fields4j/resources/YearField");

  private static final int MIN_YEAR = 1990;
  private static final int MAX_YEAR = 2100;

  private static final List<Integer> YEARS;

  static {
    YEARS = new ArrayList<>();

    for (int i = MIN_YEAR; i < MAX_YEAR; i++) {
      YEARS.add(i);
    }
  }

  public YearField() {
    setBlankItemText(BUNDLE.getString("blankChoice.text"));

    setItems(YEARS);
    setValue(LocalDate.now().getYear());
  }
}
