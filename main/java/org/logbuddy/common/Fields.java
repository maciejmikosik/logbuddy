package org.logbuddy.common;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

import org.logbuddy.LogBuddyException;

public class Fields {
  public static void set(Object instance, Field field, Object value) {
    try {
      makeAccessible(field).set(instance, value);
    } catch (IllegalAccessException e) {
      throw new LogBuddyException(e);
    }
  }

  public static Object read(Object decorable, Field field) {
    try {
      return makeAccessible(field).get(decorable);
    } catch (IllegalAccessException e) {
      throw new LogBuddyException(e);
    }
  }

  private static Field makeAccessible(Field field) {
    AccessController.doPrivileged(new PrivilegedAction<Void>() {
      public Void run() {
        field.setAccessible(true);
        return null;
      }
    });
    return field;
  }
}
