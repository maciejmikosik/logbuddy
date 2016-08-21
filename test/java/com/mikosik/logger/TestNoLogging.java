package com.mikosik.logger;

import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

public class TestNoLogging {
  private Logging logging;
  private Wrappable original;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void does_not_wrap() {
    given(original = new Wrappable());
    given(logging = new NoLogging());
    when(() -> logging.wrap(original));
    thenReturned(original);
  }

  public static class Wrappable {}
}
