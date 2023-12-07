package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.ByteBuddyCommons.delegateTo;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.logbuddy.Decorator;
import org.objenesis.ObjenesisStd;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;

public class JdkDecorator implements Decorator {
  private final Decorator decorator;

  private JdkDecorator(Decorator decorator) {
    this.decorator = decorator;
  }

  public static Decorator jdk(Decorator decorator) {
    check(decorator != null);
    return new JdkDecorator(decorator);
  }

  public <T> T decorate(T decorable) {
    check(decorable != null);
    return needsPeeling(decorable.getClass())
        ? decorator.decorate(peel(decorable))
        : decorator.decorate(decorable);
  }

  public static <T> T peel(T decorable) {
    Typing typing = peel(decorable.getClass());
    Class<?> proxyClass = new ByteBuddy()
        .subclass(typing.superclass)
        .implement(typing.interfaces)
        .method(ElementMatchers.any())
        .intercept(delegateTo(decorable))
        .make()
        .load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.INJECTION)
        .getLoaded();
    return (T) new ObjenesisStd().newInstance(proxyClass);
  }

  private static boolean needsPeeling(Class<?> type) {
    return type.getPackage() != null
        && type.getPackage().getName().startsWith("java.")
        && !Modifier.isPublic(type.getModifiers());
  }

  private static Typing peel(Class<?> type) {
    Typing typing = new Typing();
    typing.superclass = type;
    typing.interfaces = new ArrayList<>();
    while (needsPeeling(typing.superclass)) {
      typing.interfaces.addAll(asList(typing.superclass.getInterfaces()));
      typing.superclass = typing.superclass.getSuperclass();
    }
    return typing;
  }

  public String toString() {
    return format("jdk(%s)", decorator);
  }

  private static class Typing {
    Class<?> superclass;
    List<Class<?>> interfaces;
  }
}
