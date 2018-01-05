package com.fields4j.validators;

/**
 * Verifica que el nombre de usuario empiece con una letra y que contenga solo letras, dígitos y _
 */
public class UserNameValidator extends RegExpValidator {
    public UserNameValidator() {
        this("");
        setMessage(getBundle().getString("userNameError"));
    }

    public UserNameValidator(String message) {
        super("[a-zA-Z]\\w*", message);
    }
}
