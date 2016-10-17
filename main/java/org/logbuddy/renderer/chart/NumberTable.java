package org.logbuddy.renderer.chart;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

public class NumberTable {
  private final List<List<Number>> columns;
  private final int numberOfRows;

  private NumberTable(List<List<Number>> columns, int numberOfRows) {
    this.columns = columns;
    this.numberOfRows = numberOfRows;
  }

  public static NumberTable singleColumn(List<Number> column) {
    return multiColumn(asList(column));
  }

  public static NumberTable multiColumn(List<? extends List<? extends Number>> columns) {
    int numberOfRows = columns.stream()
        .mapToInt(column -> column.size())
        .max()
        .orElse(0);
    List<List<Number>> resized = columns.stream()
        .map(column -> resize(numberOfRows, column))
        .collect(toList());
    return new NumberTable(unmodifiableList(resized), numberOfRows);
  }

  private static List<Number> resize(int size, List<? extends Number> column) {
    List<Number> resized = new ArrayList<>(column);
    while (resized.size() < size) {
      resized.add(0.0);
    }
    return unmodifiableList(resized);
  }

  public List<List<Number>> columns() {
    return columns;
  }

  public int numberOfColumns() {
    return columns.size();
  }

  public int numberOfRows() {
    return numberOfRows;
  }

  public List<Number> column(int index) {
    return columns.get(index);
  }

  public DoubleSummaryStatistics statistics() {
    return columns.stream()
        .flatMap(List<Number>::stream)
        .mapToDouble(Number::doubleValue)
        .summaryStatistics();
  }
}
