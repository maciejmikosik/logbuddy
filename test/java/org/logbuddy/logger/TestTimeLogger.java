package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.logger.TimeLogger.time;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;

public class TestTimeLogger {
  private Logger logger, timeLogger;
  private Message message;
  private Instant instant;
  private ZoneOffset zone;
  private Clock clock;

  @Before
  public void before() {
    given(instant = Instant.ofEpochMilli(0));
    given(zone = ZoneOffset.UTC);
    givenTest(this);
  }

  @Test
  public void logs_time_using_clock() {
    given(instant = Instant.ofEpochMilli(0));
    given(zone = ZoneOffset.UTC);
    given(timeLogger = time(Clock.fixed(instant, zone), logger));
    when(() -> timeLogger.log(message));
    thenReturned();
    thenCalled(logger).log(message.attribute(ZonedDateTime.ofInstant(instant, zone)));
  }

  @Test
  public void logs_time_using_clock_timezone() {
    given(instant = Instant.ofEpochMilli(0));
    given(zone = ZoneOffset.ofHours(2));
    given(timeLogger = time(Clock.fixed(instant, zone), logger));
    when(() -> timeLogger.log(message));
    thenReturned();
    thenCalled(logger).log(message.attribute(ZonedDateTime.ofInstant(instant, zone)));
  }

  @Test
  public void implements_to_string() {
    given(timeLogger = time(clock, logger));
    when(timeLogger.toString());
    thenReturned(format("time(%s, %s)", clock, logger));
  }

  @Test
  public void clock_cannot_be_null() {
    given(clock = null);
    when(() -> time(clock, logger));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void logger_cannot_be_null() {
    given(logger = null);
    when(() -> time(clock, logger));
    thenThrown(LogBuddyException.class);
  }
}
