package com.mikosik.logbuddy.formatter;

import static com.mikosik.logbuddy.LogBuddyException.check;
import static java.lang.String.format;

public class Html {
  public final String content;

  private Html(String content) {
    this.content = content;
  }

  public static Html html(String content) {
    check(content != null);
    return new Html(content);
  }

  public String toString() {
    return format("html(%s)", content);
  }
}
