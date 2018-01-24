package com.fields4j.fields;

import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import javax.swing.JPanel;

import com.fields4j.core.Field;

public class DateTimeField extends Field<JPanel, JPanel, LocalDateTime> {

  private DateField dateField;
  private TimeField timeField;

  public DateTimeField() {
    super(new JPanel(new GridLayout(1, 0, 5, 0)));

    dateField = new DateField();
    dateField.setLabelsVisible(false);

    timeField = new TimeField();
    timeField.setLabelsVisible(false);

    getMainComponent().setFocusable(false);

    getValueComponent().add(dateField.getValueComponent());
    getValueComponent().add(timeField.getValueComponent());

    dateField.addValueChangeListener(event -> fireValueChangeEvent(null, null));
    timeField.addValueChangeListener(event -> fireValueChangeEvent(null, null));

    // se establece un valor valido para que el componente adquiera un tamaño adecuado
    setValue(LocalDateTime.now());

    // evitar que el campo de fecha se vuelva muy pequeño cuando se establezca null como valor
    addValueChangeListener(
        event -> {
          /*
          solo se reorganiza el layout cuando el nuevo valor no es null, para el caso
          contrario se deja el layout como esta, se asume que el valor actual es una fecha
          valida y por tanto el componente tiene un tamaño adecuado
          */
          if (event.getNewValue() != null) {
            revalidate();
          }
        });
  }

  @Override
  public boolean isEmpty() {
    if ((dateField == null) || (timeField == null)) {
      return false;
    }
    return dateField.isEmpty() && timeField.isEmpty();
  }

  @Override
  public LocalDateTime getValue() {
    if ((dateField == null) || (timeField == null)) {
      return null;
    }

    LocalDate date = dateField.getValue();
    return (date == null) ? null : LocalDateTime.of(date, timeField.getValue());
  }

  @Override
  public void setValue(LocalDateTime value) {
    if (value == null) {
      dateField.setValue(null);
      timeField.setValue(LocalTime.MIN);
    } else {
      dateField.setValue(value.toLocalDate());
      timeField.setValue(value.toLocalTime());
    }
  }

  @Override
  public void setEditable(boolean editable) {
    if ((dateField != null) && (timeField != null)) {
      dateField.setEditable(editable);
      timeField.setEditable(editable);
    }
  }

  @Override
  public void setRequired(boolean required) {
    if ((dateField != null) && (timeField != null)) {
      dateField.setRequired(required);
      timeField.setRequired(required);
    }
  }

  public LocalDate getMinDate() {
    return dateField.getMinDate();
  }

  public void setMinDate(LocalDate date) {
    dateField.setMinDate(date);
  }

  public LocalDate getMaxDate() {
    return dateField.getMaxDate();
  }

  public void setMaxDate(LocalDate date) {
    dateField.setMaxDate(date);
  }
}
