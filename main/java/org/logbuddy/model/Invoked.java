package org.logbuddy.model;

import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.Objects.hash;
import static org.logbuddy.LogBuddyException.check;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Invoked {
  public final Object instance;
  public final Method method;
  public final List<Object> arguments;

  public Invoked(Object instance, Method method, List<Object> arguments) {
    this.instance = instance;
    this.method = method;
    this.arguments = arguments;
  }

  public static Invoked invoked(Object instance, Method method, List<Object> arguments) {
    check(instance != null);
    check(method != null);
    check(arguments != null);
    return new Invoked(instance, method, unmodifiableList(new ArrayList<>(arguments)));
  }

  public boolean equals(Object object) {
    return object instanceof Invoked && equals((Invoked) object);
  }

  private boolean equals(Invoked invocation) {
    return instance == invocation.instance
        && Objects.equals(method, invocation.method)
        && Objects.equals(arguments, invocation.arguments);
  }

  public int hashCode() {
    return hash(instance, method, arguments);
  }

  public String toString() {
    return format("invoked(%s, %s, %s)", instance, method, arguments);
  }
}
