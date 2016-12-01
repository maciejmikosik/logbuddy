package org.logbuddy.renderer.chart;

import org.logbuddy.renderer.Html;

public interface ChartRenderer {
  Html render(ChartModel model);
}
