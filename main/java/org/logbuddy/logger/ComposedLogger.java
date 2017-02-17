package org.logbuddy.logger;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.logbuddy.LogBuddyException.check;

import java.util.ArrayList;
import java.util.List;

import org.logbuddy.Logger;
import org.logbuddy.Message;

public class ComposedLogger implements Logger {
  private final List<Logger> loggers;

  private ComposedLogger(List<Logger> loggers) {
    this.loggers = loggers;
  }

  public static Logger compose(Logger... loggers) {
    return new ComposedLogger(validate(loggers));
  }

  private static List<Logger> validate(Logger[] unvalidatedLoggers) {
    check(unvalidatedLoggers != null);
    List<Logger> loggers = new ArrayList<>(asList(unvalidatedLoggers));
    check(!loggers.contains(null));
    return loggers;
  }

  public void log(Message message) {
    for (Logger logger : loggers) {
      logger.log(message);
    }
  }

  public String toString() {
    return loggers.stream()
        .map(Logger::toString)
        .collect(joining(", ", "compose(", ")"));
  }
}
