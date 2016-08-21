package com.mikosik.logbuddy.logger;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.util.Objects.requireNonNull;

import java.time.Clock;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;

import com.mikosik.logbuddy.Logger;

public class TimeLogger implements Logger {
  private final Clock clock;
  private final DateTimeFormatter formatter;
  private final Logger logger;

  private TimeLogger(Clock clock, DateTimeFormatter formatter, Logger logger) {
    this.clock = clock;
    this.formatter = formatter;
    this.logger = logger;
  }

  public static Logger time(Clock clock, Logger logger) {
    requireNonNull(clock);
    requireNonNull(logger);
    DateTimeFormatter formatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(ISO_LOCAL_DATE)
        .appendLiteral('T')
        .appendValue(HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(SECOND_OF_MINUTE, 2)
        .appendFraction(NANO_OF_SECOND, 3, 3, true)
        .appendOffsetId()
        .toFormatter()
        .withZone(clock.getZone());
    return new TimeLogger(clock, formatter, logger);
  }

  public void log(String message) {
    logger.log(format("%s %s", formatter.format(clock.instant()), message));
  }
}
