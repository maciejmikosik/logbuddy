package com.mikosik.logger;

import com.mikosik.logger.logger.NoLogger;

public class NoLogging extends Logging {
  public NoLogging() {
    super(new NoLogger());
  }

  public <T> T wrap(T original) {
    return original;
  }
}
