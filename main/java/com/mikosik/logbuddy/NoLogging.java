package com.mikosik.logbuddy;

import com.mikosik.logbuddy.logger.NoLogger;

public class NoLogging extends Logging {
  public NoLogging() {
    super(new NoLogger(), object -> "");
  }

  public <T> T wrap(T original) {
    return original;
  }
}
