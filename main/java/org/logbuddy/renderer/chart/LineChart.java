package org.logbuddy.renderer.chart;

import static java.util.stream.Collectors.toList;
import static org.logbuddy.renderer.Html.html;
import static org.logbuddy.renderer.chart.Canvas.canvas;

import java.awt.Color;
import java.util.List;

import org.logbuddy.renderer.Html;

public class LineChart implements Cloneable {
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

  public LineChart bottom(double bottom) {
    return new LineChart(configuration.bottom(bottom));
  }

  public LineChart top(double top) {
    return new LineChart(configuration.top(top));
  }

  public LineChart axisColor(Color color) {
    return new LineChart(configuration.axisColor(color));
  }

  public LineChart axisWidth(double width) {
    return new LineChart(configuration.axisWidth(width));
  }

  public LineChart lineColor(Color color) {
    return new LineChart(configuration.lineColor(color));
  }

  public LineChart lineWidth(double width) {
    return new LineChart(configuration.lineWidth(width));
  }

  public LineChart dotColor(Color color) {
    return new LineChart(configuration.dotColor(color));
  }

  public LineChart dotSize(double size) {
    return new LineChart(configuration.dotSize(size));
  }

  public Html plot(List<? extends Number> values) {
    List<Double> doubles = values.stream()
        .map(number -> number.doubleValue())
        .collect(toList());
    return plotDoubles(doubles);
  }

  private Html plotDoubles(List<Double> values) {
    double bottom = configuration.bottom().orElseGet(() -> values.stream().min(Double::compare).get());
    double top = configuration.top().orElseGet(() -> values.stream().max(Double::compare).get());
    List<Double> dots = values.stream()
        .map(value -> (1 - phase(bottom, value, top)) * configuration.height())
        .collect(toList());

    Canvas canvas = canvas(configuration.width(), configuration.height());
    drawAxis(canvas, bottom, top);
    drawChart(canvas, dots);
    return html(canvas.toHtml());
  }

  private void drawChart(Canvas canvas, List<Double> dots) {
    double scaleX = 1.0 * canvas.width / dots.size();
    canvas.beginPath();
    for (int i = 0; i < dots.size() - 1; i++) {
      canvas.moveTo(i * scaleX, dots.get(i));
      canvas.lineTo((i + 1) * scaleX, dots.get(i + 1));
    }
    canvas.lineWidth(configuration.lineWidth());
    canvas.strokeStyle(configuration.lineColor());
    canvas.stroke();
    canvas.fillStyle(configuration.dotColor());
    double dotSize = configuration.dotSize();
    for (int i = 0; i < dots.size(); i++) {
      canvas.fillRect(i * scaleX - 0.5 * dotSize, dots.get(i) - 0.5 * dotSize, dotSize, dotSize);
    }
  }

  private void drawAxis(Canvas canvas, double bottom, double top) {
    double phase = phase(bottom, 0, top);
    int axisY = (int) ((1 - phase) * canvas.height);
    canvas.beginPath();
    canvas.moveTo(0, axisY);
    canvas.lineTo(canvas.width, axisY);
    canvas.lineWidth(configuration.axisWidth());
    canvas.strokeStyle(configuration.axisColor());
    canvas.stroke();
  }

  private static double phase(double begin, double value, double end) {
    return (value - begin) / (end - begin);
  }
}
