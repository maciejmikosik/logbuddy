package org.logbuddy.common;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Classes {
  public static boolean isWrapper(Class<?> type) {
    return wrappers.contains(type);
  }

  private static final Set<Class<?>> wrappers = wrappers();

  private static Set<Class<?>> wrappers() {
    Set<Class<?>> wrappers = new HashSet<>();
    wrappers.add(Byte.class);
    wrappers.add(Short.class);
    wrappers.add(Integer.class);
    wrappers.add(Long.class);
    wrappers.add(Float.class);
    wrappers.add(Double.class);
    wrappers.add(Character.class);
    wrappers.add(Boolean.class);
    return wrappers;
  }

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
