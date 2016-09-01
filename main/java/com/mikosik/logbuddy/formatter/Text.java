package com.mikosik.logbuddy.formatter;

import static com.mikosik.logbuddy.LogBuddyException.check;

public class Text {
  public final String string;

  private Text(String string) {
    this.string = string;
  }

  public static Text text(String string) {
    check(string != null);
    return new Text(string);
  }
}
