package com.mikosik.logbuddy.formatter;

import static com.mikosik.logbuddy.LogBuddyException.check;
import static java.util.Collections.unmodifiableList;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Invocation {
  public final Object instance;
  public final Method method;
  public final List<Object> arguments;

  public Invocation(Object instance, Method method, List<Object> arguments) {
    this.instance = instance;
    this.method = method;
    this.arguments = arguments;
  }

  public static Invocation invocation(Object instance, Method method, List<Object> arguments) {
    check(instance != null);
    check(method != null);
    check(arguments != null);
    List<Object> argumentsList = unmodifiableList(new ArrayList<>(arguments));
    check(!arguments.contains(null));
    return new Invocation(instance, method, argumentsList);
  }
}
