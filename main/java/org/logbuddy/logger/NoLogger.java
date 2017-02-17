package org.logbuddy.logger;

import org.logbuddy.Logger;
import org.logbuddy.Message;

public class NoLogger implements Logger {
  private NoLogger() {}

  public static Logger noLogger() {
    return new NoLogger();
  }

  public void log(Message message) {}

  public String toString() {
    return "noLogger()";
  }
}
