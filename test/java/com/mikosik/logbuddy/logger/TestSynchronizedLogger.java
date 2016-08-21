package com.mikosik.logbuddy.logger;

import static com.mikosik.logbuddy.logger.SynchronizedLogger.synchronize;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;

import com.mikosik.logbuddy.Logger;

public class TestSynchronizedLogger {
  private String message;
  private Logger logger, synchronizedLogger;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void logs_message() {
    given(synchronizedLogger = synchronize(logger));
    when(() -> synchronizedLogger.log(message));
    thenReturned();
    thenCalled(logger).log(message);
  }

  @Test
  public void checks_null() {
    when(() -> synchronize(null));
    thenThrown(NullPointerException.class);
  }
}
