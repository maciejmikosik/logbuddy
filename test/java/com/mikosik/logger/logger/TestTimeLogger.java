package com.mikosik.logger.logger;

import static com.mikosik.logger.logger.TimeLogger.time;
import static java.time.Clock.fixed;
import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import org.junit.Before;
import org.junit.Test;

import com.mikosik.logger.Logger;

public class TestTimeLogger {
  private String message;
  private Logger logger;
  private Logger timeLogger;
  private Clock clock;

  @Before
  public void before() {
    givenTest(this);
    given(clock = fixed(Instant.ofEpochMilli(0), ZoneOffset.UTC));
  }

  @Test
  public void logs_message_using_decorated_logger() {
    given(timeLogger = time(clock, logger));
    when(() -> timeLogger.log(message));
    thenReturned();
    thenCalled(logger).log(any(String.class, containsString(message)));
  }

  @Test
  public void logs_time_in_utc() {
    given(clock = fixed(Instant.ofEpochMilli(0), ZoneOffset.UTC));
    given(timeLogger = time(clock, logger));
    when(() -> timeLogger.log(message));
    thenReturned();
    thenCalled(logger).log(any(String.class, containsString("1970-01-01T00:00:00.000Z")));
  }

  @Test
  public void logs_time_in_clock_timezone() {
    given(clock = fixed(Instant.ofEpochMilli(0), ZoneOffset.ofHours(2)));
    given(timeLogger = time(clock, logger));
    when(() -> timeLogger.log(message));
    thenReturned();
    thenCalled(logger).log(any(String.class, containsString("1970-01-01T02:00:00.000+02:00")));
  }

  @Test
  public void checks_null() {
    when(() -> time(null, logger));
    thenThrown(NullPointerException.class);

    when(() -> time(clock, null));
    thenThrown(NullPointerException.class);
  }
}
