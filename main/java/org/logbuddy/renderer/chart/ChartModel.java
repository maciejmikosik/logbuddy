package org.logbuddy.renderer.chart;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableNavigableMap;
import static java.util.Objects.hash;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

public class ChartModel {
  public final NavigableMap<Double, Double> points;

  private ChartModel(NavigableMap<Double, Double> points) {
    this.points = points;
  }

  public static ChartModel chartModel(List<? extends Number> values) {
    NavigableMap<Double, Double> points = new TreeMap<>();
    for (int i = 0; i < values.size(); i++) {
      points.put((double) i, values.get(i).doubleValue());
    }
    return new ChartModel(unmodifiableNavigableMap(points));
  }

  public static ChartModel chartModel(Map<? extends Number, ? extends Number> points) {
    NavigableMap<Double, Double> pointsCopy = new TreeMap<>();
    points.entrySet().stream()
        .forEach(entry -> pointsCopy.put(
            entry.getKey().doubleValue(),
            entry.getValue().doubleValue()));
    return new ChartModel(unmodifiableNavigableMap(pointsCopy));
  }

  public boolean equals(Object object) {
    return object instanceof ChartModel && equals((ChartModel) object);
  }

  private boolean equals(ChartModel object) {
    return Objects.equals(points, object.points);
  }

  public int hashCode() {
    return hash(points);
  }

  public String toString() {
    return format("chartModel(%s)", points);
  }
}
