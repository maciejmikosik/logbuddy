package org.logbuddy.renderer.chart;

import java.awt.Color;
import java.util.Optional;

public class Configuration implements Cloneable {
  private int width = 500;
  private int height = 100;
  private Optional<Double> bottom = Optional.empty();
  private Optional<Double> top = Optional.empty();
  private Color axisColor = Color.black;
  private double axisWidth = 1;
  private Color lineColor = Color.black;
  private double lineWidth = 0.5;
  private Color dotColor = Color.black;
  private double dotSize = 2;

  public Configuration width(int width) {
    Configuration copy = copy();
    copy.width = width;
    return copy;
  }

  public int width() {
    return width;
  }

  public Configuration height(int height) {
    Configuration copy = copy();
    copy.height = height;
    return copy;
  }

  public int height() {
    return height;
  }

  public Configuration bottom(double bottom) {
    Configuration copy = copy();
    copy.bottom = Optional.of(bottom);
    return copy;
  }

  public Optional<Double> bottom() {
    return bottom;
  }

  public Configuration top(double top) {
    Configuration copy = copy();
    copy.top = Optional.of(top);
    return copy;
  }

  public Optional<Double> top() {
    return top;
  }

  public Configuration axisColor(Color color) {
    Configuration copy = copy();
    copy.axisColor = color;
    return copy;
  }

  public Color axisColor() {
    return axisColor;
  }

  public Configuration axisWidth(double width) {
    Configuration copy = copy();
    copy.axisWidth = width;
    return copy;

  }

  public double axisWidth() {
    return axisWidth;
  }

  public Configuration lineColor(Color color) {
    Configuration copy = copy();
    copy.lineColor = color;
    return copy;

  }

  public Color lineColor() {
    return lineColor;
  }

  public Configuration lineWidth(double width) {
    Configuration copy = copy();
    copy.lineWidth = width;
    return copy;
  }

  public double lineWidth() {
    return lineWidth;
  }

  public Configuration dotColor(Color color) {
    Configuration copy = copy();
    copy.dotColor = color;
    return copy;

  }

  public Color dotColor() {
    return dotColor;
  }

  public Configuration dotSize(double size) {
    Configuration copy = copy();
    copy.dotSize = size;
    return copy;

  }

  public double dotSize() {
    return dotSize;
  }

  private Configuration copy() {
    try {
      return (Configuration) this.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }
}
