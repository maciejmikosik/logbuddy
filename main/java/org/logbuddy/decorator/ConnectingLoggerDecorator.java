package org.logbuddy.decorator;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;
import org.logbuddy.logger.NoLogger;

public class ConnectingLoggerDecorator implements Decorator {
  private final Logger logger;
  private final Decorator decorator;

  private ConnectingLoggerDecorator(Logger logger, Decorator decorator) {
    this.logger = logger;
    this.decorator = decorator;
  }

  public static ConnectingLoggerDecorator connecting(Logger logger, Decorator decorator) {
    check(logger != null);
    check(decorator != null);
    return new ConnectingLoggerDecorator(logger, decorator);
  }

  public <T> T decorate(T decorable) {
    check(decorable != null);
    return decorable instanceof NoLogger
        ? (T) logger
        : decorator.decorate(decorable);
  }

  public String toString() {
    return format("connecting(%s, %s)", logger, decorator);
  }
}
