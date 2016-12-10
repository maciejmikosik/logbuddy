package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.Fields.set;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;

public class InjectingLoggerDecorator implements Decorator {
  private final Logger logger;

  private InjectingLoggerDecorator(Logger logger) {
    this.logger = logger;
  }

  public static Decorator injecting(Logger logger) {
    check(logger != null);
    return new InjectingLoggerDecorator(logger);
  }

  public <T> T decorate(T decorable) {
    check(decorable != null);
    stream(decorable.getClass().getDeclaredFields())
        .filter(field -> field.getType() == Logger.class)
        .forEach(field -> set(decorable, field, logger));
    return decorable;
  }

  public String toString() {
    return format("injecting(%s)", logger);
  }
}
