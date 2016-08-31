package com.mikosik.logbuddy;

import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

public class TestNoDecorator {
  private Decorator decorator;
  private Decorable original;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void does_not_decorate() {
    given(original = new Decorable());
    given(decorator = new NoDecorator());
    when(() -> decorator.decorate(original));
    thenReturned(original);
  }

  public static class Decorable {}
}
