package org.logbuddy.bind;

import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.logger.NoLogger.noLogger;
import static org.slf4j.impl.LogbuddyLogger.logbuddyLogger;

import org.logbuddy.Logger;
import org.slf4j.event.Level;
import org.slf4j.impl.StaticLoggerBinder;

public class Slf4jBinder {
  private final Level level;
  private final Logger logger;

  private Slf4jBinder(Level level, Logger logger) {
    this.level = level;
    this.logger = logger;
  }

  public static Slf4jBinder slf4jBinder(Level level, Logger logger) {
    check(level != null);
    check(logger != null);
    return new Slf4jBinder(level, logger);
  }

  public void bind() {
    StaticLoggerBinder.getSingleton().setLoggerFactory(name -> logbuddyLogger(level, logger));
  }

  public void unbind() {
    StaticLoggerBinder.getSingleton().setLoggerFactory(name -> logbuddyLogger(level, noLogger()));
  }
}
