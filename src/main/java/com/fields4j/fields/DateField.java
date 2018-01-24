package com.fields4j.fields;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;

import com.fields4j.core.Field;
import com.fields4j.validators.core.BaseValidator;
import com.fields4j.validators.core.ValidationException;
import com.fields4j.validators.core.Validator;
import com.toedter.calendar.IDateEditor;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JSpinnerDateEditor;

@SuppressWarnings("UseOfObsoleteDateTimeApi")
public class DateField extends Field<JDateChooser, JDateChooser, LocalDate> {

  public static SimpleDateFormat DATE_FORMAT = null;

  // indica si el usuario establecio explicitamente una fecha minima diferente de null
  private boolean minDateExplicitlySet = false;

  // indica si el usuario establecio explicitamente una fecha maxima diferente de null
  private boolean maxDateExplicitlySet = false;

  public DateField() {
    super(new JDateChooser(new JSpinnerDateEditor()));
    getValueComponent().getJCalendar().setWeekOfYearVisible(false);

    getValueComponent().setLocale(new Locale("es", "ES"));
    setToolTipText(getToolTipText());

    setupFocusBehavior();
    setupDateRangeValidator();

    if (DATE_FORMAT != null) {
      setFormat(DATE_FORMAT);
    }

    getValueComponent().addPropertyChangeListener("date", event -> {
      LocalDate oldDate = null;
      LocalDate newDate = null;

      if (event.getOldValue() != null) {
        oldDate = toLocalDate((Date) event.getOldValue());
      }

      if (event.getNewValue() != null) {
        newDate = toLocalDate((Date) event.getNewValue());
      }

              /*
              solo disparar el evento si la fecha realmente cambio, ya q el JFormattedTextField
              presente en el editor del JSpinner dispara un ValueChangeEvent cuando pierde el foco
              y no verifica si su valor realmente cambio.
              */
      if (!Objects.equals(oldDate, newDate)) {
        fireValueChangeEvent(oldDate, newDate);
      }
    });
  }

  public SimpleDateFormat getFormat() {
    IDateEditor editor = getValueComponent().getDateEditor();
    return new SimpleDateFormat(editor.getDateFormatString());
  }

  public void setFormat(SimpleDateFormat format) {
    IDateEditor editor = getValueComponent().getDateEditor();

    String currentToolTip = getToolTipText();

    if (format == null) {
      editor.setDateFormatString(null);
    } else {
      editor.setDateFormatString(format.toPattern());
    }

    setToolTipText(currentToolTip);
  }

  public LocalDate getMinDate() {
    Date minDate = getValueComponent().getMinSelectableDate();
    return (minDate == null) ? LocalDate.MIN : toLocalDate(minDate);
  }

  public void setMinDate(LocalDate date) {
    LocalDate valueBeforeMinChanged = getValue();

    Date minDate = (date == null) ? null : toDate(date);
    getValueComponent().setMinSelectableDate(minDate);

    setValue(valueBeforeMinChanged);

    adjustTodayButtonVisibility();

    minDateExplicitlySet = minDate != null;
    validateField();
  }

  public LocalDate getMaxDate() {
    Date maxDate = getValueComponent().getMaxSelectableDate();
    return (maxDate == null) ? LocalDate.MAX : toLocalDate(maxDate);
  }

  public void setMaxDate(LocalDate date) {
    LocalDate valueBeforeMaxChanged = getValue();

    Date maxDate = (date == null) ? null : toDate(date);
    getValueComponent().setMaxSelectableDate(maxDate);

    setValue(valueBeforeMaxChanged);

    adjustTodayButtonVisibility();

    maxDateExplicitlySet = maxDate != null;
    validateField();
  }

  @Override
  public boolean isEmpty() {
    return getValue() == null;
  }

  @Override
  public LocalDate getValue() {
    JSpinner spinner = (JSpinner) getValueComponent().getDateEditor()
                                                     .getUiComponent();
    JSpinner.DateEditor spinnerEditor = (JSpinner.DateEditor) spinner.getEditor();

    String renderedDate = spinnerEditor.getTextField().getText();
    Date internalDate = getValueComponent().getDate();

    /*
    este chequeo es necesario porque SpinnerDateModel inicia con la fecha actual como valor;
    para distinguir cuando se ha establecido un valor o no se verifica aqui que exista alguna
    representacion de este en el campo
    */
    if (renderedDate.isEmpty() && toDate(LocalDate.now()).equals(internalDate)) {
      return null;
    }

    return (internalDate == null) ? null : toLocalDate(internalDate);
  }

  @Override
  public void setValue(LocalDate value) {
    Date date = (value == null) ? null : toDate(value);
    getValueComponent().setDate(date);

    /*
    estableciendo explicitamente la fecha, ya q en algunos casos se actualiza internamente
    pero no se visualiza.
    */
    JSpinner spinner = (JSpinner) getValueComponent().getDateEditor()
                                                     .getUiComponent();
    JSpinner.DateEditor spinnerEditor = (JSpinner.DateEditor) spinner.getEditor();
    JFormattedTextField spinnerEditorTextField = spinnerEditor.getTextField();

    if (date == null) {
      spinnerEditorTextField.setText("");
    } else {
      spinnerEditorTextField.setText(spinnerEditor.getFormat().format(date));
      /*
      actualizar la validacion del campo ya q existia inconsistencia entre su valor y su
      visualizacion y el getValue() de este campo tiene en cuenta ambos
      */
      validateField();
    }
  }

  @Override
  public void setRequired(boolean required) {
    super.setRequired(required);
    getValueComponent().getJCalendar().setNullDateButtonVisible(!required);
  }

  @Override
  public void resetState() {
    setValue(null);
    super.resetState();
  }

  @Override
  public void setToolTipText(String text) {
    super.setToolTipText(text);
    IDateEditor editor = getValueComponent().getDateEditor();

    JSpinner spinner = (JSpinner) editor.getUiComponent();
    spinner.setToolTipText(text);
  }

  private void setupDateRangeValidator() {
    ResourceBundle bundle = ResourceBundle.getBundle("com/fields4j/resources/DateField");

    Validator<LocalDate> dateRangeValidator = new BaseValidator<LocalDate>() {
      @Override
      public void validate(LocalDate value) throws ValidationException {
        SimpleDateFormat dateFormat = getFormat();

        LocalDate minDate = getMinDate();
        LocalDate maxDate = getMaxDate();

        if (value != null) {
          String minDateStr = minDateExplicitlySet ? dateFormat.format(toDate(minDate)) : "";
          String maxDateStr = maxDateExplicitlySet ? dateFormat.format(toDate(maxDate)) : "";

          boolean isBeforeMin = minDateExplicitlySet && value.isBefore(minDate);
          boolean isAfterMax = maxDateExplicitlySet && value.isAfter(maxDate);

          if (isBeforeMin) {
            String messageFormat = bundle.getString("minDateErrorFormat");
            String message = String.format(messageFormat, minDateStr);

            if (maxDateExplicitlySet) {
              messageFormat = bundle.getString("minMaxDateErrorFormat");
              message = String.format(messageFormat, minDateStr, maxDateStr);
            }

            throw new ValidationException(message);
          }

          if (isAfterMax) {
            String messageFormat = bundle.getString("maxDateErrorFormat");
            String message = String.format(messageFormat, maxDateStr);

            if (minDateExplicitlySet) {
              messageFormat = bundle.getString("minMaxDateErrorFormat");
              message = String.format(messageFormat, minDateStr, maxDateStr);
            }

            throw new ValidationException(message);
          }
        }
      }
    };

    addValidator(dateRangeValidator);
  }

  private void setupFocusBehavior() {
    IDateEditor editor = getValueComponent().getDateEditor();

    JSpinner spinner = (JSpinner) editor.getUiComponent();
    JSpinner.DateEditor spinnerEditor = (JSpinner.DateEditor) spinner.getEditor();

    /*
    cambiar el comportamiento por defecto del editor del JSpinner debido a que cambia el valor
    sin verificarlo
    */
    JFormattedTextField textField = spinnerEditor.getTextField();
    textField.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
    textField.setHorizontalAlignment(SwingConstants.CENTER);

    getValueComponent().getJCalendar().setTodayButtonVisible(true);

    /*
    hacer que el JSpinner transfiera el foco recibido, ya que el comportamiento por defecto
    requiere dos TAB para que se muestre el foco en el editor.
    */
    getValueComponent().addFocusListener(new FocusAdapter() {
      @Override
      public void focusGained(FocusEvent e) {
        getValueComponent().transferFocus();
      }
    });
  }

  private LocalDate toLocalDate(Date date) {
    Instant instant = date.toInstant();
    ZonedDateTime zonedDateTime = instant.atZone(ZoneId.systemDefault());
    return zonedDateTime.toLocalDate();
  }

  private Date toDate(LocalDate localDate) {
    ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());
    Instant instant = zonedDateTime.toInstant();
    return Date.from(instant);
  }

  private void adjustTodayButtonVisibility() {
    LocalDate today = LocalDate.now();

    LocalDate minDate = getMinDate();
    LocalDate maxDate = getMaxDate();

    boolean isMinAfterToday = (minDate != null) && minDate.isAfter(today);
    boolean isMaxBeforeToday = (maxDate != null) && maxDate.isBefore(today);

    boolean isTodayValid = !isMinAfterToday && !isMaxBeforeToday;
    getValueComponent().getJCalendar().setTodayButtonVisible(isTodayValid);
  }
}
