package org.logbuddy.logger;

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

  public void log(Object model) {
    logger.log(model);
  }
}
