package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.logger.ThreadLogger.thread;
import static org.logbuddy.model.Property.property;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;

public class TestThreadLogger {
  private Object model;
  private Logger logger, threadLogger;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void logs_current_thread() {
    given(threadLogger = thread(logger));
    when(() -> threadLogger.log(model));
    thenReturned();
    thenCalled(logger).log(property(Thread.currentThread(), model));
  }

  @Test
  public void implements_to_string() {
    given(threadLogger = thread(logger));
    when(threadLogger.toString());
    thenReturned(format("thread(%s)", logger));
  }

  @Test
  public void checks_null() {
    when(() -> thread(null));
    thenThrown(LogBuddyException.class);
  }
}
