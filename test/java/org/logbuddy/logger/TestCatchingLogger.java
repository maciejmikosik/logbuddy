package org.logbuddy.logger;

import static java.lang.String.format;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.logbuddy.logger.CatchingLogger.catching;
import static org.logbuddy.testing.Matchers.causedBy;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willThrow;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;

public class TestCatchingLogger {
  private Logger logger, catching;
  private Object model;
  private Throwable throwable;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new RuntimeException());
  }

  @Test
  public void delegates_logging() {
    given(catching = catching(logger));
    when(() -> catching.log(model));
    thenReturned();
    thenCalled(logger).log(model);
  }

  @Test
  public void catches_exception() {
    given(catching = catching(logger));
    given(willThrow(throwable), logger).log(model);
    when(() -> catching.log(model));
    thenReturned();
  }

  @Test
  public void logs_caught_exception() {
    given(catching = catching(logger));
    given(willThrow(throwable), logger).log(model);
    when(() -> catching.log(model));
    thenCalled(logger).log(any(Throwable.class,
        allOf(instanceOf(LogBuddyException.class), causedBy(sameInstance(throwable)))));
  }

  @Test
  public void swallows_exception_from_logging_exception() {
    given(catching = catching(logger));
    given(willThrow(throwable), logger).log(model);
    given(willThrow(throwable), logger).log(throwable);
    when(() -> catching.log(model));
    thenReturned();
  }

  @Test
  public void implements_to_string() {
    given(catching = catching(logger));
    when(catching.toString());
    thenReturned(format("catching(%s)", logger));
  }

  @Test
  public void logger_cannot_be_null() {
    when(() -> catching(null));
    thenThrown(LogBuddyException.class);
  }
}
