package org.logbuddy.common;

import java.util.Iterator;

public class Classes {
  public static Iterable<Class<?>> hierarchy(Class<?> type) {
    return () -> new Iterator<Class<?>>() {
      private Class<?> next = type;

      public boolean hasNext() {
        return next != null;
      }

      public Class<?> next() {
        Class<?> result = next;
        next = next.getSuperclass();
        return result;
      }
    };
  }
}
