package com.fields4j.validators.core;

/**
 * Define una interfaza genérica para validar valores.
 *
 * @param <T> el tipo de los valores que se van a validar.
 *
 * @see BaseValidator
 */
public interface Validator <T> {
    /**
     * Determina si el valor especificado es válido o no; en caso de que no lo sea lanza un error
     * que contiene el motivo por el cual el valor no es correcto.
     *
     * @param value el valor a analizar
     *
     * @throws ValidationException cuando el valor no es válido
     */
    void validate(T value) throws ValidationException;

    /**
     * @return el mensaje que se va a incluir en los resultados de la validación cuando esta
     * concluya con errores.
     */
    String getMessage();

    /**
     * @param message mensaje que se va a incluir en los resultados de la validación cuando esta
     *                concluya con errores.
     */
    void setMessage(String message);
}
