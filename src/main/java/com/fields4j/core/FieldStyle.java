package com.fields4j.core;

import java.awt.Color;
import java.awt.Font;
import javax.swing.Icon;

public interface FieldStyle {

  Color getFieldBackground();

  Color getDisabledFieldForeground();

  Color getLabelBackground();

  Color getLabelForeground();

  Font getLabelFont();

  Color getRequiredMarkBackground();

  Color getRequiredMarkForeground();

  Font getRequiredMarkFont();

  String getRequiredMarkText();

  Icon getRequiredMarkIcon();

  Color getMainComponentBackground();

  Color getValueComponentBackground();

  Color getValueComponentForeground();

  Font getValueComponentFont();
}
