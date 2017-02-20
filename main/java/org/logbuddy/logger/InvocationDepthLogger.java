package org.logbuddy.logger;

import static java.lang.String.format;

import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.model.Invocation;
import org.logbuddy.model.InvocationDepth;
import org.logbuddy.model.Returned;
import org.logbuddy.model.Thrown;

public class InvocationDepthLogger implements Logger {
  private final Logger logger;
  private final ThreadLocal<Integer> numberOfInvocations = ThreadLocal.withInitial(() -> 0);

  private InvocationDepthLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger invocationDepth(Logger logger) {
    return new InvocationDepthLogger(logger);
  }

  public void log(Message message) {
    if (message.content() instanceof Returned || message.content() instanceof Thrown) {
      numberOfInvocations.set(numberOfInvocations.get() - 1);
    }
    logger.log(message.attribute(InvocationDepth.invocationDepth(numberOfInvocations.get())));
    if (message.content() instanceof Invocation) {
      numberOfInvocations.set(numberOfInvocations.get() + 1);
    }
  }

  public String toString() {
    return format("invocationDepth(%s)", logger);
  }
}
