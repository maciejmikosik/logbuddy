package org.logbuddy.renderer.chart;

import static java.awt.Color.BLUE;
import static org.logbuddy.renderer.Html.html;
import static org.logbuddy.renderer.chart.Canvas.canvas;
import static org.logbuddy.renderer.chart.Translation.translation;

import java.awt.Color;

import org.logbuddy.renderer.Html;
import org.logbuddy.renderer.chart.Canvas.LinearGradient;

public class SaturationChart {
  private final Configuration configuration;

  private SaturationChart(Configuration configuration) {
    this.configuration = configuration;
  }

  public static SaturationChart saturationChart() {
    return new SaturationChart(new Configuration().color(BLUE));
  }

  public SaturationChart width(int width) {
    return new SaturationChart(configuration.width(width));
  }

  public SaturationChart height(int height) {
    return new SaturationChart(configuration.height(height));
  }

  public SaturationChart minimum(double minimum) {
    return new SaturationChart(configuration.minimum(minimum));
  }

  public SaturationChart maximum(double maximum) {
    return new SaturationChart(configuration.maximum(maximum));
  }

  public SaturationChart color(Color color) {
    return new SaturationChart(configuration.color(color));
  }

  public Html plot(Data data) {
    Double min = configuration.minimum()
        .orElseGet(() -> data.points.values().stream().min(Double::compareTo).get());
    Double max = configuration.maximum()
        .orElseGet(() -> data.points.values().stream().max(Double::compareTo).get());

    Translation translation = translation()
        .sourceX(data.points.firstKey(), data.points.lastKey())
        .targetX(0, 1);

    Canvas canvas = canvas(configuration.width(), configuration.height());
    LinearGradient gradient = canvas.createLinearGradient(0, 0, canvas.width, 0);

    data.points.entrySet().stream()
        .map(point -> translation.translate(point))
        .forEach(point -> {
          double saturation = phase(min, point.getValue(), max);
          gradient.addColorStop(point.getKey(), saturate(saturation, configuration.color()));
        });
    canvas.fillStyle(gradient);
    canvas.fillRect(0, 0, canvas.width, canvas.height);
    return html(canvas.toHtml());
  }

  private static double phase(double begin, double value, double end) {
    return (value - begin) / (end - begin);
  }

  private static Color saturate(double saturation, Color color) {
    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    return Color.getHSBColor(hsb[0], (float) (saturation * hsb[1]), hsb[2]);
  }
}
