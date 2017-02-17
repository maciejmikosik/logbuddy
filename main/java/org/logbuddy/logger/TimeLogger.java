package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.logbuddy.Logger;
import org.logbuddy.Message;

public class TimeLogger implements Logger {
  private final Clock clock;
  private final Logger logger;

  private TimeLogger(Clock clock, Logger logger) {
    this.clock = clock;
    this.logger = logger;
  }

  public static Logger time(Clock clock, Logger logger) {
    check(clock != null);
    check(logger != null);
    return new TimeLogger(clock, logger);
  }

  public void log(Message message) {
    logger.log(message.attribute(ZonedDateTime.now(clock)));
  }

  public String toString() {
    return format("time(%s, %s)", clock, logger);
  }
}
