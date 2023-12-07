package org.logbuddy.common;

import static org.logbuddy.common.Classes.makeAccessible;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;

public class ByteBuddyCommons {
  public static MethodDelegation delegateTo(Object instance) {
    return MethodDelegation.to(new Handler(instance));
  }

  public static class Handler {
    private final Object original;

    public Handler(Object original) {
      this.original = original;
    }

    @RuntimeType
    public Object handle(@Origin Method method, @AllArguments Object[] arguments) throws Throwable {
      try {
        return makeAccessible(method).invoke(original, arguments);
      } catch (InvocationTargetException e) {
        throw e.getCause();
      }
    }
  }
}
