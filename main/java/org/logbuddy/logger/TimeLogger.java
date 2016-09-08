package org.logbuddy.logger;

import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.model.Property.property;

import java.time.Clock;
import java.time.ZonedDateTime;

import org.logbuddy.Logger;

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

  public void log(Object model) {
    logger.log(property(ZonedDateTime.now(clock), model));
  }

}
