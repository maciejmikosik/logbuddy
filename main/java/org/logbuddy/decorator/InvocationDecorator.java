package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.Classes.makeAccessible;
import static org.logbuddy.model.Invocation.invocation;
import static org.logbuddy.model.Returned.returned;
import static org.logbuddy.model.Thrown.thrown;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

public class InvocationDecorator implements Decorator {
  private final Objenesis objenesis = new ObjenesisStd();
  private final ByteBuddy byteBuddy = new ByteBuddy();

  private final Logger logger;

  private InvocationDecorator(Logger logger) {
    this.logger = logger;
  }

  public static Decorator invocationDecorator(Logger logger) {
    check(logger != null);
    return new InvocationDecorator(logger);
  }

  public <T> T decorate(T decorable) {
    check(decorable != null);
    Class<?> decorableType = byteBuddy
        .subclass(peel(decorable.getClass()))
        .method(ElementMatchers.any())
        .intercept(MethodDelegation.to(new DecorateHandler(decorable)))
        .make()
        .load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.INJECTION)
        .getLoaded();
    return (T) objenesis.newInstance(decorableType);
  }

  private static Class<?> peel(Class<?> type) {
    return type.isAnonymousClass()
        ? type.getSuperclass() != Object.class
            ? type.getSuperclass()
            : type.getInterfaces().length > 0
                ? type.getInterfaces()[0]
                : Object.class
        : type;
  }

  public class DecorateHandler {
    private final Object original;

    public DecorateHandler(Object original) {
      this.original = original;
    }

    @RuntimeType
    public Object handle(@Origin Method method, @AllArguments Object[] arguments) throws Throwable {
      logger.log(invocation(original, method, asList(arguments)));
      try {
        Object result = makeAccessible(method).invoke(original, arguments);
        logger.log(returned(result));
        return result;
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        logger.log(thrown(cause));
        throw cause;
      }
    }
  }

  public String toString() {
    return format("invocationDecorator(%s)", logger);
  }
}
