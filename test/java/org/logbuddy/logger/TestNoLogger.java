package org.logbuddy.logger;

import static org.logbuddy.logger.NoLogger.noLogger;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Logger;

public class TestNoLogger {
  private Logger logger;
  private Object model;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void does_nothing() {
    given(logger = noLogger());
    when(() -> logger.log(model));
    thenReturned();
  }
}
