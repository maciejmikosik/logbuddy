package com.mikosik.logbuddy.decorator;

import static com.mikosik.logbuddy.LogBuddyException.check;
import static com.mikosik.logbuddy.formatter.Invocation.invocation;
import static com.mikosik.logbuddy.formatter.Returned.returned;
import static com.mikosik.logbuddy.formatter.Thrown.thrown;
import static java.util.Arrays.asList;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import com.mikosik.logbuddy.Decorator;
import com.mikosik.logbuddy.Depth;
import com.mikosik.logbuddy.Formatter;
import com.mikosik.logbuddy.Logger;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

public class DefaultDecorator implements Decorator {
  private final Objenesis objenesis = new ObjenesisStd();
  private final ByteBuddy byteBuddy = new ByteBuddy();

  private final Depth depth = new Depth();
  private final Logger logger;
  private final Formatter formatter;

  private DefaultDecorator(Logger logger, Formatter formatter) {
    this.logger = new DepthLogger(depth, logger);
    this.formatter = formatter;
  }

  public static Decorator defaultDecorator(Logger logger, Formatter formatter) {
    check(logger != null);
    check(formatter != null);
    return new DefaultDecorator(logger, formatter);
  }

  public <T> T decorate(T original) {
    Class<?> decorableType = byteBuddy
        .subclass(original.getClass())
        .method(ElementMatchers.any())
        .intercept(MethodDelegation.to(new DecorateHandler(original)))
        .make()
        .load(original.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
        .getLoaded();
    return (T) objenesis.newInstance(decorableType);
  }

  public class DecorateHandler {
    private final Object original;

    public DecorateHandler(Object original) {
      this.original = original;
    }

    @RuntimeType
    public Object handle(@Origin Method method, @AllArguments Object[] arguments) throws Throwable {
      logger.log(formatter.format(invocation(original, method, asList(arguments))));
      try {
        Object result = depth.invoke(() -> method.invoke(original, arguments));
        logger.log(formatter.format(returned(result)));
        return result;
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        logger.log(formatter.format(thrown(cause)));
        throw cause;
      }
    }
  }

  private static class DepthLogger implements Logger {
    private final Depth depth;
    private final Logger logger;

    public DepthLogger(Depth depth, Logger logger) {
      this.depth = depth;
      this.logger = logger;
    }

    public void log(String message) {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < depth.get(); i++) {
        builder.append("\t");
      }
      builder.append(message);
      logger.log(builder.toString());
    }
  }
}
