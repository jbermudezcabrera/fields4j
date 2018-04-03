package com.fields4j.fields.internal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.text.JTextComponent;

import com.jidesoft.hints.ListDataIntelliHints;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class CustomListDataIntelliHints<E> extends ListDataIntelliHints<E> {

  private DefaultListCellRenderer listCellRenderer;

  private MatchMode matchMode = MatchMode.STARTS_WTIH;

  private Font font = null;
  private Color foregroundColor = null;

  public CustomListDataIntelliHints(JTextComponent comp, List<E> completionList) {
    super(comp, completionList);

    listCellRenderer = new CustomListCellRenderer();
  }

  @Override
  public JComponent createHintsComponent() {
    JComponent hintsComponent = super.createHintsComponent();

    getList().setCellRenderer(listCellRenderer);

    return hintsComponent;
  }

  @Override
  public boolean updateHints(Object context) {
    if (context != null) {
      String contextStr = context.toString();
      JTextComponent textComponent = getTextComponent();

      boolean allTextSelected = contextStr.equals(textComponent.getSelectedText());

      if (allTextSelected) {
        setListData(getCompletionList().toArray());
        return true;
      }
    }

    return super.updateHints(context);
  }

  @Override
  protected boolean compare(Object context, E element) {
    String listEntry = (element == null) ? "" : element.toString();
    String contextStr = context.toString();

    Pair<Integer, Integer> match = getMatchRange(listEntry, contextStr);

    switch (matchMode) {
      case STARTS_WTIH:
        return match.getLeft() == 0;

      case CONTAINS:
        return match.getLeft() >= 0;
    }

    return false;
  }

  @Override
  public void acceptHint(Object selected) {
    super.acceptHint(selected);

    if (getTextComponent() instanceof JFormattedTextField) {
      ((JFormattedTextField) getTextComponent()).setValue(selected);
    }

    getTextComponent().selectAll();
  }

  public void setForegroundColor(Color foregroundColor) {
    this.foregroundColor = foregroundColor;
  }

  public void setFont(Font font) {
    this.font = font.deriveFont(Font.PLAIN);
  }

  public MatchMode getMatchMode() {
    return matchMode;
  }

  public void setMatchMode(MatchMode matchMode) {
    this.matchMode = matchMode;
  }

  private Pair<Integer, Integer> getMatchRange(String str, String pattern) {
    int start;

    if (isCaseSensitive()) {
      start = str.indexOf(pattern);
    } else {
      start = StringUtils.indexOfIgnoreCase(str, pattern);
    }

    return Pair.of(start, start + pattern.length());
  }

  public enum MatchMode {
    STARTS_WTIH, CONTAINS
  }

  private class CustomListCellRenderer extends DefaultListCellRenderer {

    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {

      Component component = super.getListCellRendererComponent(list, value, index, isSelected,
                                                               cellHasFocus);

      Object context = getContext();
      String valueStr = "";

      if ((context != null) && (value != null)) {
        valueStr = value.toString();

        Pair<Integer, Integer> range = getMatchRange(valueStr, context.toString());
        Integer start = range.getLeft();
        Integer end = range.getRight();

        if ((start >= 0) && (end >= 0)) {
          String beforeMatch = valueStr.substring(0, start);
          String afterMatch = valueStr.substring(end);

          String match = valueStr.substring(start, end);

          int R = foregroundColor.getRed();
          int G = foregroundColor.getGreen();
          int B = foregroundColor.getBlue();

          String format = "<html>%s<strong style='color: rgb(%d,%d,%d)'>%s</strong>%s</html>";
          valueStr = String.format(format, beforeMatch, R, G, B, match, afterMatch);
        }
      }

      ((JLabel) component).setText(valueStr);
      component.setFont(font);
      return component;
    }
  }
}
