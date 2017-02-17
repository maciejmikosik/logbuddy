package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import org.logbuddy.Logger;
import org.logbuddy.Message;

public class SynchronizedLogger implements Logger {
  private final Logger logger;

  private SynchronizedLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger synchronize(Logger logger) {
    check(logger != null);
    return new SynchronizedLogger(logger);
  }

  public synchronized void log(Message message) {
    logger.log(message);
  }

  public String toString() {
    return format("synchronize(%s)", logger);
  }
}
