package com.fields4j.fields;

import java.util.ResourceBundle;

import com.fields4j.validators.RegExpValidator;

public class PersonNameField extends StringField {

  public PersonNameField() {
    setText(PersonNameField.class.getSimpleName());

    ResourceBundle bundle = ResourceBundle.getBundle("com/fields4j/resources/PersonNameField");

    String message = bundle.getString("validationErrorMessage");
    addValidator(new RegExpValidator("\\p{Lu}[\\p{L}. ]*", message));
  }
}
