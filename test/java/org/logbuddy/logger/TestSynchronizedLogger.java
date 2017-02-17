package org.logbuddy.logger;

import static java.lang.String.format;
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
import org.logbuddy.Message;

public class TestSynchronizedLogger {
  private Logger logger, synchronizedLogger;
  private Message message;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void logs_original_model() {
    given(synchronizedLogger = synchronize(logger));
    when(() -> synchronizedLogger.log(message));
    thenReturned();
    thenCalled(logger).log(message);
  }

  @Test
  public void logger_cannot_be_null() {
    given(logger = null);
    when(() -> synchronize(logger));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void implements_to_string() {
    given(synchronizedLogger = synchronize(logger));
    when(synchronizedLogger.toString());
    thenReturned(format("synchronize(%s)", logger));
  }
}
