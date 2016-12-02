package org.logbuddy.common;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Throwables {
  public static String stackTrace(Throwable throwable) {
    StringWriter buffer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(buffer));
    return buffer.toString();
  }
}
