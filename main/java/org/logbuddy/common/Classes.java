package org.logbuddy.common;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.Iterator;

public class Classes {
  public static boolean isStatic(Member member) {
    return Modifier.isStatic(member.getModifiers());
  }

  public static <A extends AccessibleObject> A makeAccessible(A accessibleObject) {
    accessibleObject.setAccessible(true);
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
