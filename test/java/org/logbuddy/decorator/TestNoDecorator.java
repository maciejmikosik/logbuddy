package org.logbuddy.decorator;

import static org.logbuddy.decorator.NoDecorator.noDecorator;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Decorator;

public class TestNoDecorator {
  private Decorator decorator;
  private Decorable decorable;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void does_not_decorate() {
    given(decorator = noDecorator());
    when(decorator.decorate(decorable));
    thenReturned(decorable);
  }

  @Test
  public void implements_to_string() {
    given(decorator = noDecorator());
    when(decorator.toString());
    thenReturned("noDecorator()");
  }

  private static class Decorable {}
}
