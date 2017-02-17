package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import org.logbuddy.Logger;
import org.logbuddy.Message;

public class ThreadLogger implements Logger {
  private final Logger logger;

  private ThreadLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger thread(Logger logger) {
    check(logger != null);
    return new ThreadLogger(logger);
  }

  public void log(Message message) {
    logger.log(message.attribute(Thread.currentThread()));
  }

  public String toString() {
    return format("thread(%s)", logger);
  }
}
