package org.logbuddy.renderer.chart;

import static java.awt.Color.BLUE;
import static org.logbuddy.renderer.Html.html;
import static org.logbuddy.renderer.chart.Canvas.canvas;

import java.awt.Color;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.logbuddy.renderer.Html;

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

  public SaturationChart bottom(double bottom) {
    return new SaturationChart(configuration.bottom(bottom));
  }

  public SaturationChart top(double top) {
    return new SaturationChart(configuration.top(top));
  }

  public SaturationChart color(Color color) {
    return new SaturationChart(configuration.color(color));
  }

  public Html plot(NumberTable table) {
    DoubleSummaryStatistics statistics = table.statistics();
    double bottom = configuration.bottom().orElse(statistics.getMin());
    double top = configuration.top().orElse(statistics.getMax());

    Canvas canvas = canvas(configuration.width(), configuration.height());
    for (int y = 0; y < table.numberOfColumns(); y++) {
      List<Number> list = table.column(y);
      for (int x = 0; x < list.size(); x++) {
        canvas.beginPath();
        canvas.moveTo(configuration.width() * x / list.size(), 1.0 * configuration.height() * y / table.numberOfColumns());
        canvas.lineTo(configuration.width() * x / list.size(), 1.0 * configuration.height() * (y + 1) / table.numberOfColumns());
        canvas.lineWidth(1.0 * configuration.width() / list.size());
        double saturation = phase(bottom, list.get(x).doubleValue(), top);
        canvas.strokeStyle(saturate(saturation, configuration.color()));
        canvas.stroke();
      }
    }
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
