package org.logbuddy.logger;

import static java.lang.String.format;
import static org.logbuddy.model.Depth.depth;

import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.model.Invocation;
import org.logbuddy.model.Returned;
import org.logbuddy.model.Thrown;

public class StackTraceLogger implements Logger {
  private final Logger logger;
  private final ThreadLocal<Integer> numberOfInvocations = new ThreadLocal<Integer>() {
    protected Integer initialValue() {
      return 0;
    }
  };

  private StackTraceLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger stackTrace(Logger logger) {
    return new StackTraceLogger(logger);
  }

  public void log(Message message) {
    if (message.content() instanceof Returned || message.content() instanceof Thrown) {
      numberOfInvocations.set(numberOfInvocations.get() - 1);
    }
    logger.log(message.attribute(depth(numberOfInvocations.get())));
    if (message.content() instanceof Invocation) {
      numberOfInvocations.set(numberOfInvocations.get() + 1);
    }
  }

  public String toString() {
    return format("stackTrace(%s)", logger);
  }
}
