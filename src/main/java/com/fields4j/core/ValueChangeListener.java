package com.fields4j.core;

import java.util.EventListener;

/**
 * Un {@code ValueChangeEvent} se crea cuando un {@code Field} cambia su valor. Puede registrar un
 * {@code ValueChangeListener} con un campo fuente para ser notificado de cualquier cambio de valor.
 *
 * @param <V> el tipo de los valores contenidos en los {@code ValueChangeEvent} esperados
 */
public interface ValueChangeListener<V> extends EventListener {

  /**
   * Este método es invocado cuando el valor del campo cambia.
   *
   * @param event un objeto {@link ValueChangeEvent} que describe la fuente del evento y el valor
   *              viejo y nuevo.
   */
  void valueChanged(ValueChangeEvent<V> event);
}
