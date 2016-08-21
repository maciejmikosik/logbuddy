package com.mikosik.logger.logger;

import static java.util.Objects.requireNonNull;

import com.mikosik.logger.Logger;

public class SynchronizedLogger implements Logger {
  private final Logger logger;

  private SynchronizedLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger synchronize(Logger logger) {
    requireNonNull(logger);
    return new SynchronizedLogger(logger);
  }

  public synchronized void log(String message) {
    logger.log(message);
  }
}
