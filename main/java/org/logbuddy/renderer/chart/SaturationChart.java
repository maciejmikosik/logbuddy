package org.logbuddy.renderer.chart;

import static java.awt.Color.BLUE;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.logbuddy.renderer.Html.html;
import static org.logbuddy.renderer.chart.Canvas.canvas;

import java.awt.Color;
import java.util.List;
import java.util.stream.Stream;

import org.logbuddy.renderer.Html;

public class SaturationChart implements Cloneable {
  private final Configuration configuration;

  private SaturationChart(Configuration configuration) {
    this.configuration = configuration;
  }

  public static SaturationChart saturationChart() {
    return new SaturationChart(new Configuration().dotColor(BLUE));
  }

  public SaturationChart width(int width) {
    return new SaturationChart(configuration.width(width));
  }

  public SaturationChart height(int height) {
    return new SaturationChart(configuration.height(height));
  }

  public SaturationChart bottom(double bottom) {
    return new SaturationChart(configuration.bottom(bottom));
  }

  public SaturationChart top(double top) {
    return new SaturationChart(configuration.top(top));
  }

  public SaturationChart dotColor(Color color) {
    return new SaturationChart(configuration.dotColor(color));
  }

  public Html plot(List<? extends Number> model) {
    return plotAll(asList(model));
  }

  public Html plotAll(List<List<? extends Number>> model) {
    List<List<Double>> doubleModel = model.stream()
        .map(list -> list
            .stream()
            .map(value -> value.doubleValue())
            .collect(toList()))
        .collect(toList());
    return plotDoubles(doubleModel);
  }

  private Html plotDoubles(List<List<Double>> values) {
    double bottom = configuration.bottom().orElseGet(() -> streamDeep(values).min(Double::compare).get());
    double top = configuration.top().orElseGet(() -> streamDeep(values).max(Double::compare).get());

    Canvas canvas = canvas(configuration.width(), configuration.height());
    for (int y = 0; y < values.size(); y++) {
      List<Double> list = values.get(y);
      for (int x = 0; x < list.size(); x++) {
        canvas.beginPath();
        canvas.moveTo(configuration.width() * x / list.size(), 1.0 * configuration.height() * y / values.size());
        canvas.lineTo(configuration.width() * x / list.size(), 1.0 * configuration.height() * (y + 1) / values.size());
        canvas.lineWidth(1.0 * configuration.width() / list.size());
        double saturation = phase(bottom, list.get(x), top);
        canvas.strokeStyle(saturate(saturation, configuration.dotColor()));
        canvas.stroke();
      }
    }
    return html(canvas.toHtml());
  }

  private static double phase(double begin, double value, double end) {
    return (value - begin) / (end - begin);
  }

  private static <E> Stream<E> streamDeep(List<List<E>> list) {
    return list.stream().flatMap(innerList -> innerList.stream());
  }

  private static Color saturate(double saturation, Color color) {
    float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
    return Color.getHSBColor(hsb[0], (float) (saturation * hsb[1]), hsb[2]);
  }
}
