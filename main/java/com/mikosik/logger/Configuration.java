package com.mikosik.logger;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.io.Writer;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

public class Configuration {
  private final Objenesis objenesis = new ObjenesisStd();
  private final ByteBuddy byteBuddy = new ByteBuddy();

  private final Writer writer;
  private final Predicate<Method> predicate;

  public Configuration(Predicate<Method> predicate, Writer writer) {
    this.predicate = predicate;
    this.writer = writer;
  }

  public <T> T wrap(T original) {
    Class<?> wrappedType = byteBuddy
        .subclass(original.getClass())
        .method(ElementMatchers.any())
        .intercept(InvocationHandlerAdapter.of(invocationHandlerWrapping(original)))
        .make()
        .load(getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
        .getLoaded();
    return (T) objenesis.newInstance(wrappedType);
  }

  private <T> InvocationHandler invocationHandlerWrapping(T original) {
    return new InvocationHandler() {
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (predicate.test(method)) {
          writer.write(formatInvocation(original, method, args));
          writer.write("\n");
        }
        try {
          Object result = method.invoke(original, args);
          if (predicate.test(method)) {
            writer.write(formatReturned(result));
            writer.write("\n");
          }
          return result;
        } catch (InvocationTargetException e) {
          Throwable cause = e.getCause();
          writer.write(formatThrown(cause));
          writer.write("\n");
          throw cause;
        }
      }
    };
  }

  private String formatReturned(Object result) {
    return format("returned %s", result);
  }

  private String formatThrown(Throwable throwable) {
    return format("thrown %s", throwable);
  }

  private static String formatInvocation(Object instance, Method method, Object[] arguments) {
    String argumentsString = stream(arguments)
        .map(Object::toString)
        .collect(joining(", "));
    return format("%s.%s(%s)", instance, method.getName(), argumentsString);
  }
}
