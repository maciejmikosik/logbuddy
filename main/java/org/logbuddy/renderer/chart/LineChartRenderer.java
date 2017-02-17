package org.logbuddy.renderer.chart;

import static org.logbuddy.renderer.chart.Canvas.canvas;
import static org.logbuddy.renderer.chart.Translation.translation;

import java.awt.Color;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

public class LineChartRenderer implements ChartRenderer {
  private final Configuration configuration;

  private LineChartRenderer(Configuration configuration) {
    this.configuration = configuration;
  }

  public static LineChartRenderer lineChartRenderer() {
    return new LineChartRenderer(new Configuration());
  }

  public LineChartRenderer width(int width) {
    return new LineChartRenderer(configuration.width(width));
  }

  public LineChartRenderer height(int height) {
    return new LineChartRenderer(configuration.height(height));
  }

  public LineChartRenderer minimum(double minimum) {
    return new LineChartRenderer(configuration.minimum(minimum));
  }

  public LineChartRenderer maximum(double maximum) {
    return new LineChartRenderer(configuration.maximum(maximum));
  }

  public LineChartRenderer color(Color color) {
    return new LineChartRenderer(configuration.color(color));
  }

  public LineChartRenderer axisColor(Color color) {
    return new LineChartRenderer(configuration.axisColor(color));
  }

  public LineChartRenderer axisWidth(double width) {
    return new LineChartRenderer(configuration.axisWidth(width));
  }

  public LineChartRenderer lineWidth(double width) {
    return new LineChartRenderer(configuration.lineWidth(width));
  }

  public LineChartRenderer dotSize(double size) {
    return new LineChartRenderer(configuration.dotSize(size));
  }

  public String render(ChartModel model) {
    Translation translation = translation()
        .sourceX(model.points.firstKey(), model.points.lastKey())
        .sourceY(
            configuration.minimum()
                .orElseGet(() -> model.points.values().stream().min(Double::compareTo).get()),
            configuration.maximum()
                .orElseGet(() -> model.points.values().stream().max(Double::compareTo).get()))
        .targetX(0, configuration.width())
        .targetY(configuration.height(), 0);

    Canvas canvas = canvas(configuration.width(), configuration.height());
    drawAxis(model, canvas, translation);
    drawLine(model, canvas, translation);
    drawPoints(model, canvas, translation);
    return canvas.toHtml().replace("\n", " ");
  }

  private void drawAxis(ChartModel model, Canvas canvas, Translation translation) {
    Entry<Double, Double> axisBegin = translation.translate(
        new SimpleImmutableEntry<>(model.points.firstKey(), 0.0));
    Entry<Double, Double> axisEnd = translation.translate(
        new SimpleImmutableEntry<>(model.points.lastKey(), 0.0));
    canvas.beginPath();
    canvas.moveTo(axisBegin.getKey(), axisBegin.getValue());
    canvas.lineTo(axisEnd.getKey(), axisEnd.getValue());
    canvas.lineWidth(configuration.axisWidth());
    canvas.strokeStyle(configuration.axisColor());
    canvas.stroke();
  }

  private void drawLine(ChartModel model, Canvas canvas, Translation translation) {
    canvas.beginPath();
    Entry<Double, Double> start = translation.translate(model.points.firstEntry());
    canvas.moveTo(start.getKey(), start.getValue());
    model.points.entrySet().stream()
        .map(point -> translation.translate(point))
        .skip(1)
        .forEach(point -> canvas.lineTo(point.getKey(), point.getValue()));
    canvas.lineWidth(configuration.lineWidth());
    canvas.strokeStyle(configuration.color());
    canvas.stroke();
  }

  private void drawPoints(ChartModel model, Canvas canvas, Translation translation) {
    canvas.fillStyle(configuration.color());
    double dotSize = configuration.dotSize();
    model.points.entrySet().stream()
        .map(point -> translation.translate(point))
        .forEach(point -> {
          canvas.fillRect(
              point.getKey() - 0.5 * dotSize,
              point.getValue() - 0.5 * dotSize,
              dotSize,
              dotSize);
        });
  }
}
