package com.fields4j.fields;

import com.fields4j.validators.UserNameValidator;

public class UserNameField extends StringField {

  public UserNameField() {
    setText(UserNameField.class.getSimpleName());
    addValidator(new UserNameValidator());
  }
}
