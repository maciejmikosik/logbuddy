package com.mikosik.logbuddy.formatter;

import static com.mikosik.logbuddy.LogBuddyException.check;

import java.lang.reflect.Method;

public class InvokedMethod {
  public final Method method;

  private InvokedMethod(Method method) {
    this.method = method;
  }

  public static InvokedMethod invoked(Method method) {
    check(method != null);
    return new InvokedMethod(method);
  }
}
