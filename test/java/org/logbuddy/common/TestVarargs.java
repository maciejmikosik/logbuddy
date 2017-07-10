package org.logbuddy.common;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.logbuddy.common.Varargs.varargs;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class TestVarargs {
  private Varargs<Foo> varargs;
  private Foo a, b, c, d;
  private List<Foo> list, elements;
  private Foo[] array;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void creates_empty_list() {
    when(varargs()
        .toList());
    thenReturned(emptyList());
  }

  @Test
  public void creates_singleton_list() {
    when(varargs(a)
        .toList());
    thenReturned(asList(a));
  }

  @Test
  public void creates_list_from_array() {
    when(varargs(a, b, c)
        .toList());
    thenReturned(asList(a, b, c));
  }

  @Test
  public void creates_list_from_list() {
    when(varargs(asList(a, b, c))
        .toList());
    thenReturned(asList(a, b, c));
  }

  @Test
  public void forbids_null_elements_accepts_correct_list() {
    when(varargs(a, b, c)
        .forbidNullElements()
        .toList());
    thenReturned(asList(a, b, c));
  }

  @Test
  public void forbids_null_elements_rejects_wrong_list() {
    given(varargs = varargs(a, null, c)
        .forbidNullElements());
    when(() -> varargs.toList());
    thenThrown(NullPointerException.class);
  }

  @Test
  public void unmodifiable_prevents_modification() {
    given(elements = varargs(new ArrayList<>(asList(a, b, c)))
        .unmodifiable()
        .toList());
    when(() -> elements.add(d));
    thenThrown(UnsupportedOperationException.class);
  }

  @Test
  public void defensive_copies_array() {
    given(array = new Foo[] { a, b, c });
    given(elements = varargs(array)
        .defensiveCopy()
        .toList());
    when(array[1] = d);
    thenEqual(elements, asList(a, b, c));
  }

  @Test
  public void defensive_copies_list() {
    given(list = new ArrayList<>(asList(a, b, c)));
    given(elements = varargs(list)
        .defensiveCopy()
        .toList());
    when(list.add(d));
    thenEqual(elements, asList(a, b, c));
  }

  @Test
  public void on_error_wraps_exception() {
    given(varargs = varargs(a, null, b)
        .forbidNullElements()
        .onErrorThrow(FooException::new));
    when(() -> varargs.toList());
    thenThrown(FooException.class);
  }

  private static class Foo {}

  private static class FooException extends RuntimeException {
    public FooException(Throwable cause) {
      super(cause);
    }
  }
}
