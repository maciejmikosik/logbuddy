package org.logbuddy.renderer.chart;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.logbuddy.renderer.chart.Data.data;
import static org.logbuddy.renderer.chart.LineChart.lineChart;
import static org.logbuddy.renderer.chart.SaturationChart.saturationChart;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertTrue;

import java.util.List;

import org.junit.runner.RunWith;
import org.logbuddy.renderer.Html;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TestCharts {
  @Quackery
  public static Suite charts_html_is_rendered_as_single_line() {
    List<Chart> charts = asList(lineChart(), saturationChart());
    return suite("charts html is rendered as single line")
        .addAll(charts, TestCharts::testHtmlIsRenderedAsSingleLine);
  }

  private static Case testHtmlIsRenderedAsSingleLine(Chart chart) {
    return newCase(format("%s html is rendered as single line", chart), () -> {
      Data data = data(asList(1, 2, 3));
      Html html = chart.plot(data);
      assertTrue(!html.body.contains("\n"));
    });
  }
}
