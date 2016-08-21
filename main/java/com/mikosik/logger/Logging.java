package com.mikosik.logger;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

public class Logging {
  private final Objenesis objenesis = new ObjenesisStd();
  private final ByteBuddy byteBuddy = new ByteBuddy();

  private final Depth depth = new Depth();
  private final Logger logger;

  public Logging(Logger logger) {
    this.logger = new DepthLogger(depth, logger);
  }

  public <T> T wrap(T original) {
    Class<?> wrappedType = byteBuddy
        .subclass(original.getClass())
        .method(ElementMatchers.any())
        .intercept(MethodDelegation.to(new LoggingHandler(original)))
        .make()
        .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
        .getLoaded();
    return (T) objenesis.newInstance(wrappedType);
  }

  public class LoggingHandler {
    private final Object original;

    public LoggingHandler(Object original) {
      this.original = original;
    }

    @RuntimeType
    public Object handle(@Origin Method method, @AllArguments Object[] arguments) throws Throwable {
      logger.log(formatInvocation(original, method, arguments));
      try {
        Object result = depth.invoke(() -> method.invoke(original, arguments));
        logger.log(formatReturned(result));
        return result;
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        logger.log(formatThrown(cause));
        throw cause;
      }
    }
  }

  private String formatReturned(Object result) {
    return format("returned %s", result);
  }

  private String formatThrown(Throwable throwable) {
    return format("thrown %s", throwable);
  }

  private String formatInvocation(Object instance, Method method, Object[] arguments) {
    String argumentsString = stream(arguments)
        .map(Object::toString)
        .collect(joining(", "));
    return format("%s.%s(%s)", instance, method.getName(), argumentsString);
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
