package org.logbuddy.common;

import java.lang.reflect.AccessibleObject;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;

public class Classes {
  public static <A extends AccessibleObject> A makeAccessible(A accessibleObject) {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      public Void run() {
        accessibleObject.setAccessible(true);
        return null;
      }
    });
    return accessibleObject;
  }

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
