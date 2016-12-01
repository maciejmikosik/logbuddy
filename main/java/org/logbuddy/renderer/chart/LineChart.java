package org.logbuddy.renderer.chart;

import static org.logbuddy.renderer.Html.html;
import static org.logbuddy.renderer.chart.Canvas.canvas;
import static org.logbuddy.renderer.chart.Translation.translation;

import java.awt.Color;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Map.Entry;

import org.logbuddy.renderer.Html;

public class LineChart implements Chart {
  private final Configuration configuration;

  private LineChart(Configuration configuration) {
    this.configuration = configuration;
  }

  public static LineChart lineChart() {
    return new LineChart(new Configuration());
  }

  public LineChart width(int width) {
    return new LineChart(configuration.width(width));
  }

  public LineChart height(int height) {
    return new LineChart(configuration.height(height));
  }

  public LineChart minimum(double minimum) {
    return new LineChart(configuration.minimum(minimum));
  }

  public LineChart maximum(double maximum) {
    return new LineChart(configuration.maximum(maximum));
  }

  public LineChart color(Color color) {
    return new LineChart(configuration.color(color));
  }

  public LineChart axisColor(Color color) {
    return new LineChart(configuration.axisColor(color));
  }

  public LineChart axisWidth(double width) {
    return new LineChart(configuration.axisWidth(width));
  }

  public LineChart lineWidth(double width) {
    return new LineChart(configuration.lineWidth(width));
  }

  public LineChart dotSize(double size) {
    return new LineChart(configuration.dotSize(size));
  }

  public Html plot(Data data) {
    Translation translation = translation()
        .sourceX(data.points.firstKey(), data.points.lastKey())
        .sourceY(
            configuration.minimum()
                .orElseGet(() -> data.points.values().stream().min(Double::compareTo).get()),
            configuration.maximum()
                .orElseGet(() -> data.points.values().stream().max(Double::compareTo).get()))
        .targetX(0, configuration.width())
        .targetY(configuration.height(), 0);

    Canvas canvas = canvas(configuration.width(), configuration.height());
    drawAxis(data, canvas, translation);
    drawLine(data, canvas, translation);
    drawPoints(data, canvas, translation);
    return html(canvas.toHtml().replace("\n", " "));
  }

  private void drawAxis(Data data, Canvas canvas, Translation translation) {
    Entry<Double, Double> axisBegin = translation.translate(
        new SimpleImmutableEntry<>(data.points.firstKey(), 0.0));
    Entry<Double, Double> axisEnd = translation.translate(
        new SimpleImmutableEntry<>(data.points.lastKey(), 0.0));
    canvas.beginPath();
    canvas.moveTo(axisBegin.getKey(), axisBegin.getValue());
    canvas.lineTo(axisEnd.getKey(), axisEnd.getValue());
    canvas.lineWidth(configuration.axisWidth());
    canvas.strokeStyle(configuration.axisColor());
    canvas.stroke();
  }

  private void drawLine(Data data, Canvas canvas, Translation translation) {
    canvas.beginPath();
    Entry<Double, Double> start = translation.translate(data.points.firstEntry());
    canvas.moveTo(start.getKey(), start.getValue());
    data.points.entrySet().stream()
        .map(point -> translation.translate(point))
        .skip(1)
        .forEach(point -> canvas.lineTo(point.getKey(), point.getValue()));
    canvas.lineWidth(configuration.lineWidth());
    canvas.strokeStyle(configuration.color());
    canvas.stroke();
  }

  private void drawPoints(Data data, Canvas canvas, Translation translation) {
    canvas.fillStyle(configuration.color());
    double dotSize = configuration.dotSize();
    data.points.entrySet().stream()
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
