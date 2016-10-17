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

  public SaturationChart minimum(double minimum) {
    return new SaturationChart(configuration.minimum(minimum));
  }

  public SaturationChart maximum(double maximum) {
    return new SaturationChart(configuration.maximum(maximum));
  }

  public SaturationChart color(Color color) {
    return new SaturationChart(configuration.color(color));
  }

  public Html plot(NumberTable table) {
    DoubleSummaryStatistics statistics = table.statistics();
    double min = configuration.minimum().orElse(statistics.getMin());
    double max = configuration.maximum().orElse(statistics.getMax());
    double pointWidth = 1.0 * configuration.width() / table.numberOfRows();
    double pointHeight = 1.0 * configuration.height() / table.numberOfColumns();

    Canvas canvas = canvas(configuration.width(), configuration.height());
    for (int y = 0; y < table.numberOfColumns(); y++) {
      List<Number> column = table.column(y);
      for (int x = 0; x < column.size(); x++) {
        double saturation = phase(min, column.get(x).doubleValue(), max);
        canvas.fillStyle(saturate(saturation, configuration.color()));
        canvas.fillRect(x * pointWidth, y * pointHeight, pointWidth, pointHeight);
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
