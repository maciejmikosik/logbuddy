package org.logbuddy.logger;

import static java.util.stream.Collectors.joining;
import static org.logbuddy.common.Varargs.varargs;

import java.util.List;

import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;

public class ComposedLogger implements Logger {
  private final List<? extends Logger> loggers;

  private ComposedLogger(List<? extends Logger> loggers) {
    this.loggers = loggers;
  }

  public static Logger compose(Logger... loggers) {
    return new ComposedLogger(varargs(loggers)
        .defensiveCopy()
        .forbidNullElements()
        .onErrorThrow(LogBuddyException::new)
        .toList());
  }

  public static Logger compose(List<? extends Logger> loggers) {
    return new ComposedLogger(varargs(loggers)
        .defensiveCopy()
        .forbidNullElements()
        .onErrorThrow(LogBuddyException::new)
        .toList());
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
