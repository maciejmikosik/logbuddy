package org.logbuddy.renderer.chart;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.logbuddy.renderer.chart.NumberTable.multiColumn;
import static org.logbuddy.renderer.chart.NumberTable.singleColumn;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.DoubleSummaryStatistics;
import java.util.List;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Test;

public class TestNumberTable {
  private Number a, b, c, d, e, f, g, h, i, x;
  private NumberTable table;
  private List<List<Number>> numbers;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void creates_single_column_table() {
    when(table = singleColumn(asList(a, b, c)));
    thenEqual(table.columns(), asList(asList(a, b, c)));
    thenEqual(table.numberOfColumns(), 1);
    thenEqual(table.numberOfRows(), 3);
  }

  @Test
  public void creates_multi_column_table() {
    when(table = multiColumn(asList(asList(a, b, c), asList(d, e, f))));
    thenEqual(table.columns(), asList(asList(a, b, c), asList(d, e, f)));
    thenEqual(table.numberOfColumns(), 2);
    thenEqual(table.numberOfRows(), 3);
  }

  @Test
  public void creates_no_column_table() {
    when(table = multiColumn(asList()));
    thenEqual(table.columns(), asList());
    thenEqual(table.numberOfColumns(), 0);
    thenEqual(table.numberOfRows(), 0);
  }

  @Test
  public void counts_non_full_rows() {
    given(table = multiColumn(asList(asList(a), asList(b, c, d), asList(e, f))));
    when(table.numberOfRows());
    thenReturned(3);
  }

  @Test
  public void gets_selected_column() {
    given(table = multiColumn(asList(asList(a, b, c), asList(d, e, f), asList(g, h, i))));
    when(table.column(1));
    thenReturned(asList(d, e, f));
  }

  @Test
  public void fills_empty_cells_with_zeroes() {
    given(table = multiColumn(asList(asList(a), asList(b, c, d), asList(e, f))));
    when(table.column(0));
    thenReturned(asList(a, 0.0, 0.0));
  }

  @Test
  public void numbers_are_defensive_copied() {
    given(numbers = list(list(a, b), list(c, d)));
    given(table = multiColumn(numbers));
    when(numbers.get(0).set(0, x));
    thenEqual(table.columns().get(0).get(0), a);
  }

  @Test
  public void cell_is_immutable() {
    given(numbers = list(list(a, b), list(c, d)));
    given(table = multiColumn(numbers));
    when(() -> table.columns().get(0).set(0, x));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void column_is_immutable() {
    given(numbers = list(list(a, b), list(c, d)));
    given(table = multiColumn(numbers));
    when(() -> table.columns().set(0, asList(x)));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void calculates_summary_statistics() {
    given(numbers = asList(asList(1, 2), asList(3, 4)));
    given(table = multiColumn(numbers));
    when(table.statistics());
    thenReturned(equalTo(asList(1, 2, 3, 4).stream()
        .mapToDouble(Integer::doubleValue)
        .summaryStatistics()));
  }

  private static <E> List<E> list(E... elements) {
    return new ArrayList<>(asList(elements));
  }

  private static Matcher<DoubleSummaryStatistics> equalTo(DoubleSummaryStatistics statistics) {
    return new TypeSafeMatcher<DoubleSummaryStatistics>() {
      public void describeTo(Description description) {
        description.appendText(format("equalTo(%s)", statistics));
      }

      protected boolean matchesSafely(DoubleSummaryStatistics item) {
        return statistics.getCount() == item.getCount()
            && statistics.getSum() == item.getSum()
            && statistics.getAverage() == item.getAverage()
            && statistics.getMin() == item.getMin()
            && statistics.getMax() == item.getMax();
      }
    };
  }
}
