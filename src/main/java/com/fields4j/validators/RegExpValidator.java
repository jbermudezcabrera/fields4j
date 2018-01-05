package com.fields4j.validators;

import com.fields4j.validators.core.BaseValidator;
import com.fields4j.validators.core.ValidationException;

import java.util.regex.Pattern;

/**
 * Clase que realiza una validación contra una expresión regular.
 *
 * @see Pattern
 */
public class RegExpValidator extends BaseValidator<String> {

    private Pattern pattern;

    public RegExpValidator(String regexp) {
        this(regexp, "");
        setMessage(getBundle().getString("regExpError"));
    }

    /**
     * Construye un validador de expresiones regulares, vea {@link Pattern} para las
     * especificaciones de estas.
     *
     * @param regexp  expresión regular contra la que se va a comparar.
     * @param message mensaje a incluir en la {@code ValidationException} cuando el valor no sea
     *                válido.
     */
    public RegExpValidator(String regexp, String message) {
        super(message);
        pattern = Pattern.compile(regexp);
    }

    /**
     * Construye un validador de expresiones regulares, vea {@link Pattern} para las
     * especificaciones de estas.
     *
     * @param regexp  expresión regular compilada contra la que se va a comparar.
     * @param message mensaje a incluir en la {@code ValidationException} cuando el valor no sea
     *                válido.
     */
    public RegExpValidator(Pattern regexp, String message) {
        super(message);
        pattern = regexp;
    }

    @Override
    public void validate(String value) throws ValidationException {
        boolean valid = pattern.matcher(value).matches();

        if (!valid) {
            throw new ValidationException(getMessage());
        }
    }

}
