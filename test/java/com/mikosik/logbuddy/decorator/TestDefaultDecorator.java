package com.mikosik.logbuddy.decorator;

import static com.mikosik.logbuddy.decorator.DefaultDecorator.defaultDecorator;
import static org.hamcrest.Matchers.instanceOf;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

import com.mikosik.logbuddy.Decorator;
import com.mikosik.logbuddy.Formatter;
import com.mikosik.logbuddy.Logger;

public class TestDefaultDecorator {
  private Logger logger;
  private Formatter formatter;
  private Decorator decorator;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void decorates_decorated_object() {
    given(decorator = defaultDecorator(logger, formatter));
    when(() -> decorator.decorate(decorator.decorate(new Decorable())));
    thenReturned(instanceOf(Decorable.class));
  }

  public static class Decorable {}
}
