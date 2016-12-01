package org.logbuddy.logger;

import org.logbuddy.Logger;

public class NoLogger implements Logger {
  private NoLogger() {}

  public static Logger noLogger() {
    return new NoLogger();
  }

  public void log(Object model) {}
}
