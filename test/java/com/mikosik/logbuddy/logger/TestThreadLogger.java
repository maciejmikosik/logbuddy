package com.mikosik.logbuddy.logger;

import static com.mikosik.logbuddy.logger.ThreadLogger.thread;
import static java.lang.String.format;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

import com.mikosik.logbuddy.Logger;

public class TestThreadLogger {
  private Logger logger, threadLogger;
  private String message;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void logs_thread_and_message() {
    given(threadLogger = thread(logger));
    when(() -> threadLogger.log(message));
    thenCalled(logger).log(format("%s %s", Thread.currentThread().toString(), message));
  }
}
