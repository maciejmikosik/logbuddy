package org.logbuddy;

public class LogBuddyException extends RuntimeException {
  public LogBuddyException() {}

  public LogBuddyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }

  public LogBuddyException(String message, Throwable cause) {
    super(message, cause);
  }

  public LogBuddyException(String message) {
    super(message);
  }

  public LogBuddyException(Throwable cause) {
    super(cause);
  }

  public static void check(boolean condition) {
    if (!condition) {
      throw new LogBuddyException();
    }
  }
}
