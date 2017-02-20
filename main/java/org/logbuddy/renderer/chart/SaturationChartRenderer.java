package org.logbuddy.renderer.chart;

import static java.awt.Color.BLUE;
import static org.logbuddy.renderer.chart.Canvas.canvas;
import static org.logbuddy.renderer.chart.Translation.translation;

import java.awt.Color;

import org.logbuddy.renderer.chart.Canvas.LinearGradient;

public class SaturationChartRenderer implements ChartRenderer {
  private final Configuration configuration;

  private SaturationChartRenderer(Configuration configuration) {
    this.configuration = configuration;
  }

  public static SaturationChartRenderer saturationChartRenderer() {
    return new SaturationChartRenderer(new Configuration().color(BLUE));
  }

  public SaturationChartRenderer width(int width) {
    return new SaturationChartRenderer(configuration.width(width));
  }

  public SaturationChartRenderer height(int height) {
    return new SaturationChartRenderer(configuration.height(height));
  }

  public SaturationChartRenderer minimum(double minimum) {
    return new SaturationChartRenderer(configuration.minimum(minimum));
  }

  public SaturationChartRenderer maximum(double maximum) {
    return new SaturationChartRenderer(configuration.maximum(maximum));
  }

  public SaturationChartRenderer color(Color color) {
    return new SaturationChartRenderer(configuration.color(color));
  }

  public String render(ChartModel model) {
    Double min = configuration.minimum()
        .orElseGet(() -> model.points.values().stream().min(Double::compareTo).get());
    Double max = configuration.maximum()
        .orElseGet(() -> model.points.values().stream().max(Double::compareTo).get());

    Translation translation = translation()
        .sourceX(model.points.firstKey(), model.points.lastKey())
        .targetX(0, 1);

    Canvas canvas = canvas(configuration.width(), configuration.height());
    LinearGradient gradient = canvas.createLinearGradient(0, 0, canvas.width, 0);

    model.points.entrySet().stream()
        .map(point -> translation.translate(point))
        .forEach(point -> {
          double saturation = phase(min, point.getValue(), max);
          gradient.addColorStop(point.getKey(), saturate(saturation, configuration.color()));
        });
    canvas.fillStyle(gradient);
    canvas.fillRect(0, 0, canvas.width, canvas.height);
    return canvas.toHtml().replace("\n", " ");
  }

  private static double phase(double begin, double value, double end) {
    return (value - begin) / (end - begin);
  }

  private static Color saturate(double saturation, Color color) {
    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    return Color.getHSBColor(hsb[0], (float) (saturation * hsb[1]), hsb[2]);
  }
}
