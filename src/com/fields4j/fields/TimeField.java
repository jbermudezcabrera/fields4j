package com.fields4j.fields;

import com.fields4j.core.Field;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.time.*;
import java.util.Date;
import java.util.Objects;

@SuppressWarnings("UseOfObsoleteDateTimeApi")
public class TimeField extends Field<JSpinner, JSpinner, LocalTime> {
  public TimeField() {
    super(new JSpinner(new SpinnerDateModel()));

    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(getValueComponent(), "hh:mm a");
    getValueComponent().setEditor(dateEditor);

    getValueComponent().addChangeListener(e -> fireValueChangeEvent(null, null));

    getValueComponent()
        .addFocusListener(
            new FocusAdapter() {
              @Override
              public void focusGained(FocusEvent e) {
                getValueComponent().transferFocus();
              }
            });
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public LocalTime getValue() {
    return toLocalTime((Date) getValueComponent().getValue());
  }

  @Override
  public void setValue(LocalTime value) {
    Objects.requireNonNull(value);
    getValueComponent().setValue(toDate(value));
  }

  private LocalTime toLocalTime(Date date) {
    Instant instant = date.toInstant();
    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
    return zonedDateTime.toLocalTime();
  }

  private Date toDate(LocalTime localTime) {
    LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);
    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());

    Instant instant = zonedDateTime.toInstant();
    return Date.from(instant);
  }
}
