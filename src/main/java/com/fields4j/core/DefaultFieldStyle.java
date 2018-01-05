package com.fields4j.core;

import javax.swing.*;
import java.awt.*;

public class DefaultFieldStyle implements FieldStyle {

  private Color fieldBackground         = UIManager.getDefaults().getColor("Panel.background");
  private Color disabledFieldForeground = UIManager.getDefaults().getColor(
      "Label.disabledForeground");

  private Color textBackground = fieldBackground;
  private Color textForeground = UIManager.getDefaults().getColor("Label.foreground");
  private Font  textFont       = UIManager.getDefaults().getFont("Label.font");

  private Color  requiredMarkBackground = fieldBackground;
  private Color  requiredMarkForeground = Color.RED;
  private Font   requiredMarkFont       = UIManager.getDefaults().getFont("Label.font");
  private String requiredMarkText       = " *";
  private Icon   requiredMarkIcon       = null;

  private Font  valueComponentFont       = UIManager.getDefaults().getFont("TextField.font");
  private Color valueComponentBackground = UIManager.getDefaults().getColor("TextField.background");
  private Color valueComponentForeground = UIManager.getDefaults().getColor("TextField.foreground");

  private Color mainComponentBackground = fieldBackground;

  @Override
  public Color getFieldBackground() {
    return fieldBackground;
  }

  @Override
  public Color getDisabledFieldForeground() {
    return disabledFieldForeground;
  }

  @Override
  public Color getLabelBackground() {
    return textBackground;
  }

  @Override
  public Color getLabelForeground() {
    return textForeground;
  }

  @Override
  public Font getLabelFont() {
    return textFont;
  }

  @Override
  public Color getRequiredMarkBackground() {
    return requiredMarkBackground;
  }

  @Override
  public Color getRequiredMarkForeground() {
    return requiredMarkForeground;
  }

  @Override
  public Font getRequiredMarkFont() {
    return requiredMarkFont;
  }

  @Override
  public String getRequiredMarkText() {
    return requiredMarkText;
  }

  @Override
  public Icon getRequiredMarkIcon() {
    return requiredMarkIcon;
  }

  @Override
  public Color getMainComponentBackground() {
    return mainComponentBackground;
  }

  @Override
  public Color getValueComponentBackground() {
    return valueComponentBackground;
  }

  @Override
  public Color getValueComponentForeground() {
    return valueComponentForeground;
  }

  @Override
  public Font getValueComponentFont() {
    return valueComponentFont;
  }
}
