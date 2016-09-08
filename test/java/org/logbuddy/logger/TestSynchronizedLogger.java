package org.logbuddy.logger;

import static org.logbuddy.logger.SynchronizedLogger.synchronize;
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

public class TestSynchronizedLogger {
  private Logger logger, synchronizedLogger;
  private Object model;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void logs_original_model() {
    given(synchronizedLogger = synchronize(logger));
    when(() -> synchronizedLogger.log(model));
    thenReturned();
    thenCalled(logger).log(model);
  }

  @Test
  public void logger_cannot_be_null() {
    given(logger = null);
    when(() -> synchronize(logger));
    thenThrown(LogBuddyException.class);
  }
}
