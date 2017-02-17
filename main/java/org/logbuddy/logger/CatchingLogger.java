package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.Message.message;

import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;

public class CatchingLogger implements Logger {
  private final Logger logger;

  private CatchingLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger catching(Logger logger) {
    check(logger != null);
    return new CatchingLogger(logger);
  }

  public void log(Message message) {
    try {
      logger.log(message);
    } catch (Throwable throwable) {
      try {
        logger.log(message(new LogBuddyException(throwable)));
      } catch (Throwable e) {
        e.printStackTrace(System.err);
      }
    }
  }

  public String toString() {
    return format("catching(%s)", logger);
  }
}
