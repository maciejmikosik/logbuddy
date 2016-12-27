package org.logbuddy.common;

import static org.logbuddy.common.Classes.makeAccessible;

import java.lang.reflect.Field;

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
}
