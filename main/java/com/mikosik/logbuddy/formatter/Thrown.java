package com.mikosik.logbuddy.formatter;

import static com.mikosik.logbuddy.LogBuddyException.check;

public class Thrown {
  public final Throwable throwable;

  private Thrown(Throwable throwable) {
    this.throwable = throwable;
  }

  public static Thrown thrown(Throwable throwable) {
    check(throwable != null);
    return new Thrown(throwable);
  }
}
