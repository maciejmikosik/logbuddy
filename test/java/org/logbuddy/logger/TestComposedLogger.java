package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.logger.ComposedLogger.compose;
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

public class TestComposedLogger {
  private Logger loggerA, loggerB, loggerC;
  private Logger composed;
  private Message message;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void logs_using_all_loggers() {
    given(composed = compose(loggerA, loggerB, loggerC));
    when(() -> composed.log(message));
    thenReturned();
    thenCalled(loggerA).log(message);
    thenCalled(loggerB).log(message);
    thenCalled(loggerC).log(message);
  }

  @Test
  public void implements_to_string() {
    given(composed = compose(loggerA, loggerB, loggerC));
    when(composed.toString());
    thenReturned(format("compose(%s, %s, %s)", loggerA, loggerB, loggerC));
  }

  @Test
  public void logger_cannot_be_null() {
    given(loggerB = null);
    when(() -> compose(loggerA, loggerB, loggerC));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void array_cannot_be_null() {
    when(() -> compose((Logger[]) null));
    thenThrown(LogBuddyException.class);
  }
}
