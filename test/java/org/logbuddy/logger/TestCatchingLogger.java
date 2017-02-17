package org.logbuddy.logger;

import static java.lang.String.format;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.sameInstance;
import static org.logbuddy.Message.message;
import static org.logbuddy.logger.CatchingLogger.catching;
import static org.logbuddy.testing.Matchers.causedBy;
import static org.logbuddy.testing.Matchers.withContent;
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
import org.logbuddy.Message;

public class TestCatchingLogger {
  private Logger logger, catching;
  private Message message;
  private Throwable throwable;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new RuntimeException());
  }

  @Test
  public void delegates_logging() {
    given(catching = catching(logger));
    when(() -> catching.log(message));
    thenReturned();
    thenCalled(logger).log(message);
  }

  @Test
  public void catches_exception() {
    given(catching = catching(logger));
    given(willThrow(throwable), logger).log(message);
    when(() -> catching.log(message));
    thenReturned();
  }

  @Test
  public void logs_caught_exception() {
    given(catching = catching(logger));
    given(willThrow(throwable), logger).log(message);
    when(() -> catching.log(message));
    thenCalled(logger).log(any(Message.class,
        withContent(allOf(instanceOf(LogBuddyException.class), causedBy(sameInstance(throwable))))));
  }

  @Test
  public void swallows_exception_from_logging_exception() {
    given(catching = catching(logger));
    given(willThrow(throwable), logger).log(message);
    given(willThrow(throwable), logger).log(message(throwable));
    when(() -> catching.log(message));
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
