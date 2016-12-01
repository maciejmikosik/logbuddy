package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import org.logbuddy.Logger;

public class SynchronizedLogger implements Logger {
  private final Logger logger;

  private SynchronizedLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger synchronize(Logger logger) {
    check(logger != null);
    return new SynchronizedLogger(logger);
  }

  public synchronized void log(Object model) {
    logger.log(model);
  }

  public String toString() {
    return format("synchronize(%s)", logger);
  }
}
