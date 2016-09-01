package com.mikosik.logbuddy.decorator;

import static com.mikosik.logbuddy.decorator.NoDecorator.noDecorator;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

import com.mikosik.logbuddy.Decorator;

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
    given(decorator = noDecorator());
    when(() -> decorator.decorate(original));
    thenReturned(original);
  }

  public static class Decorable {}
}
