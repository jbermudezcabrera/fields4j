package com.fields4j;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

public final class FieldUtils {
  private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
      "com/fields4j/resources/FieldUtils");

  private FieldUtils() {
  }

  /**
   * Devuelve la imagen ubicada en {@code filePath} como un {@link Icon}.
   *
   * @return la imagen deseada
   */
  public static Icon getIcon(String filePath) {
    URL resource = FieldUtils.class.getResource(filePath);
    return new ImageIcon(resource);
  }

  /**
   * Muestra los mensajes provistos en una ventana de información con un título por defecto.
   *
   * @param messages mensajes a mostrar, se mostrará uno por línea
   */
  public static void showInfo(String... messages) {
    showInfo(BUNDLE.getString("information.title"), messages);
  }

  /**
   * Muestra los mensajes provistos en una ventana de información con el título especificado.
   *
   * @param title    título de la ventana de información
   * @param messages mensajes a mostrar, se mostrará uno por línea
   */
  public static void showInfo(String title, String[] messages) {
    JOptionPane.showMessageDialog(null, messages, title, JOptionPane.INFORMATION_MESSAGE);
  }
}
