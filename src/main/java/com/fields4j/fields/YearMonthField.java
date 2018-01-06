package com.fields4j.fields;

import com.fields4j.core.Field;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.Month;

public class YearMonthField extends Field<JPanel, JPanel, LocalDate> {
  private YearField yearField;
  private MonthField monthField;

  public YearMonthField() {
    super(new JPanel(new BorderLayout()));

    yearField = new YearField();
    yearField.setLabelsVisible(false);

    monthField = new MonthField();
    monthField.setLabelsVisible(false);

    getMainComponent().setFocusable(false);

    SingleSelectionField<String> ssfMonths = monthField.getValueComponent();

    Dimension size = ssfMonths.getPreferredSize();
    ssfMonths.setMinimumSize(new Dimension(40, size.height));
    ssfMonths.setPreferredSize(ssfMonths.getMinimumSize());

    JPanel valueComponent = getValueComponent();
    valueComponent.add(yearField.getValueComponent(), BorderLayout.WEST);
    valueComponent.add(ssfMonths, BorderLayout.CENTER);

    yearField.addValueChangeListener(event -> {
      // resetar el valor del mes si no se selecciono ningun año
      if(yearField.isBlankItemSelected()){
        monthField.resetState();
      }

      // solo se puede seleccionar un mes si se selecciono un año primero
      monthField.setEditable(!yearField.isEmpty());

      fireValueChangeEvent(null, null);
    });

    monthField.addValueChangeListener(event -> fireValueChangeEvent(null, null));

    // se establece un valor valido para que el componente adquiera un tamaño adecuado
    setValue(LocalDate.now());

    setMonthOptional(false);
  }

  @Override
  public boolean isEmpty() {
    if ((yearField == null) || (monthField == null)) {
      return false;
    }

    if (isMonthOptional()) {
      return yearField.isEmpty();
    }

    return yearField.isEmpty() && monthField.isEmpty();
  }

  @Override
  public LocalDate getValue() {
    if ((yearField == null) || (monthField == null)) {
      return null;
    }

    Integer year = yearField.getValue();
    if (year == null) {
      return null;
    }

    if (isMonthOptional()) {
      return LocalDate.of(year, 1, 1);
    }

    return LocalDate.of(year, monthField.getValue(), 1);
  }

  @Override
  public void setValue(LocalDate value) {
    if (value == null) {
      yearField.setValue(LocalDate.now().getYear());
      monthField.setValue(LocalDate.now().getMonth());
    } else {
      yearField.setValue(value.getYear());
      monthField.setValue(value.getMonth());
    }
  }

  @Override
  public void setEditable(boolean editable) {
    if ((yearField != null) && (monthField != null)) {
      yearField.setEditable(editable);
      monthField.setEditable(editable);
    }
  }

  @Override
  public boolean isRequired() {
    if ((yearField != null)) {
      return yearField.isRequired();
    }
    return false;
  }

  @Override
  public void setRequired(boolean required) {
    if ((yearField != null) && (monthField != null)) {
      yearField.setRequired(required);
      monthField.setRequired(required && !isMonthOptional());
    }
  }

  @Override
  public void resetState() {
    yearField.resetState();
    monthField.resetState();

    super.resetState();
  }

  public boolean isMonthSet() {
    return !monthField.isEmpty();
  }

  public boolean isMonthOptional() {
    return monthField.isMonthOptional();
  }

  public void setMonthOptional(boolean optional) {
    monthField.setMonthOptional(optional);
  }

  public int getYear() {
    return yearField.getValue();
  }

  public Month getMonth() {
    return monthField.getValue();
  }
}
