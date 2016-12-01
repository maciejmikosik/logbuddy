package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.model.Property.property;

import org.logbuddy.Logger;

public class ThreadLogger implements Logger {
  private final Logger logger;

  private ThreadLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger thread(Logger logger) {
    check(logger != null);
    return new ThreadLogger(logger);
  }

  public void log(Object model) {
    logger.log(property(Thread.currentThread(), model));
  }

  public String toString() {
    return format("thread(%s)", logger);
  }
}
