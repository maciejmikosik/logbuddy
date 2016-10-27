package org.logbuddy.renderer.chart;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableNavigableMap;
import static java.util.Objects.hash;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

public class Data {
  public final NavigableMap<Double, Double> points;

  private Data(NavigableMap<Double, Double> points) {
    this.points = points;
  }

  public static Data data(List<? extends Number> values) {
    NavigableMap<Double, Double> points = new TreeMap<>();
    for (int i = 0; i < values.size(); i++) {
      points.put((double) i, values.get(i).doubleValue());
    }
    return new Data(unmodifiableNavigableMap(points));
  }

  public static Data data(Map<? extends Number, ? extends Number> points) {
    NavigableMap<Double, Double> pointsCopy = new TreeMap<>();
    points.entrySet().stream()
        .forEach(entry -> pointsCopy.put(
            entry.getKey().doubleValue(),
            entry.getValue().doubleValue()));
    return new Data(unmodifiableNavigableMap(pointsCopy));
  }

  public boolean equals(Object object) {
    return object instanceof Data && equals((Data) object);
  }

  private boolean equals(Data object) {
    return Objects.equals(points, object.points);
  }

  public int hashCode() {
    return hash(points);
  }

  public String toString() {
    return format("data(%s)", points);
  }
}
