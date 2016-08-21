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
import net.bytebuddy.matcher.ElementMatchers;

public class Logging {
  private final Objenesis objenesis = new ObjenesisStd();
  private final ByteBuddy byteBuddy = new ByteBuddy();

  private final Depth depth = new Depth();
  private final Logger logger;

  public Logging(Logger logger) {
    this.logger = logger;
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

  private String formatDepth() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < depth.get(); i++) {
      builder.append("\t");
    }
    return builder.toString();
  }

  private String formatReturned(Object result) {
    return format("%sreturned %s", formatDepth(), result);
  }

  private String formatThrown(Throwable throwable) {
    return format("%sthrown %s", formatDepth(), throwable);
  }

  private String formatInvocation(Object instance, Method method, Object[] arguments) {
    String argumentsString = stream(arguments)
        .map(Object::toString)
        .collect(joining(", "));
    return format("%s%s.%s(%s)", formatDepth(), instance, method.getName(), argumentsString);
  }
}
