package com.mikosik.logbuddy.logger;

import static java.lang.String.format;

import com.mikosik.logbuddy.Logger;

public class ThreadLogger implements Logger {
  private final Logger logger;

  private ThreadLogger(Logger logger) {
    this.logger = logger;
  }

  public static Logger thread(Logger logger) {
    return new ThreadLogger(logger);
  }

  public void log(String message) {
    logger.log(format("%s %s", Thread.currentThread().toString(), message));
  }
}
