package com.fields4j.fields;

import com.fields4j.validators.EmailValidator;

public class EmailField extends StringField {

  public EmailField() {
    setText(EmailField.class.getSimpleName());
    addValidator(new EmailValidator());
  }
}
