package org.logbuddy.logger;

import static org.logbuddy.model.Depth.depth;

import org.logbuddy.Logger;
import org.logbuddy.model.Invocation;
import org.logbuddy.model.Returned;
import org.logbuddy.model.Thrown;

public class StackTraceLogger implements Logger {
  private final Logger logger;
  private int numberOfInvocations = 0;

  private StackTraceLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger stackTrace(Logger logger) {
    return new StackTraceLogger(logger);
  }

  public void log(Object model) {
    if (model instanceof Returned || model instanceof Thrown) {
      numberOfInvocations--;
    }
    logger.log(depth(numberOfInvocations, model));
    if (model instanceof Invocation) {
      numberOfInvocations++;
    }
  }
}
