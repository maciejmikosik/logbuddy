package com.mikosik.logbuddy.formatter;

public class Returned {
  public final Object object;

  private Returned(Object object) {
    this.object = object;
  }

  public static Returned returned(Object object) {
    return new Returned(object);
  }
}
