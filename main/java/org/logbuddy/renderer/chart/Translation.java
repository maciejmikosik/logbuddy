package org.logbuddy.renderer.chart;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

public class Translation {
  private final double sourceLeft, sourceRight;
  private final double sourceBottom, sourceTop;
  private final double targetLeft, targetRight;
  private final double targetBottom, targetTop;

  private Translation(
      double sourceLeft, double sourceRight,
      double sourceBottom, double sourceTop,
      double targetLeft, double targetRight,
      double targetBottom, double targetTop) {
    this.sourceLeft = sourceLeft;
    this.sourceRight = sourceRight;
    this.sourceBottom = sourceBottom;
    this.sourceTop = sourceTop;
    this.targetLeft = targetLeft;
    this.targetRight = targetRight;
    this.targetBottom = targetBottom;
    this.targetTop = targetTop;
  }

  public static Translation translation() {
    return new Translation(0, 1, 0, 1, 0, 1, 0, 1);
  }

  public Translation sourceX(double minimum, double maximum) {
    return new Translation(
        minimum, maximum,
        sourceBottom, sourceTop,
        targetLeft, targetRight,
        targetBottom, targetTop);
  }

  public Translation sourceY(double minimum, double maximum) {
    return new Translation(
        sourceLeft, sourceRight,
        minimum, maximum,
        targetLeft, targetRight,
        targetBottom, targetTop);
  }

  public Translation targetX(double minimum, double maximum) {
    return new Translation(
        sourceLeft, sourceRight,
        sourceBottom, sourceTop,
        minimum, maximum,
        targetBottom, targetTop);
  }

  public Translation targetY(double minimum, double maximum) {
    return new Translation(
        sourceLeft, sourceRight,
        sourceBottom, sourceTop,
        targetLeft, targetRight,
        minimum, maximum);
  }

  public Entry<Double, Double> translate(Entry<Double, Double> point) {
    return new SimpleImmutableEntry(
        translate(
            sourceLeft, sourceRight,
            targetLeft, targetRight,
            point.getKey()),
        translate(
            sourceBottom, sourceTop,
            targetBottom, targetTop,
            point.getValue()));
  }

  private static double translate(
      double sourceMinimum, double sourceMaximum,
      double targetMinimum, double targetMaximum,
      double point) {
    double phase = (point - sourceMinimum) / (sourceMaximum - sourceMinimum);
    return (1 - phase) * targetMinimum + phase * targetMaximum;
  }
}
