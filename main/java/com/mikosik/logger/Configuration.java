package com.mikosik.logger;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

import java.io.IOException;
import java.io.Writer;
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

public class Configuration {
  private final Objenesis objenesis = new ObjenesisStd();
  private final ByteBuddy byteBuddy = new ByteBuddy();

  private final Writer writer;
  private int depth = 0;

  public Configuration(Writer writer) {
    this.writer = writer;
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
      writeDepth();
      writer.write(formatInvocation(original, method, arguments));
      writer.write("\n");
      try {
        depth++;
        Object result = method.invoke(original, arguments);
        depth--;
        writeDepth();
        writer.write(formatReturned(result));
        writer.write("\n");
        return result;
      } catch (InvocationTargetException e) {
        depth--;
        Throwable cause = e.getCause();
        writeDepth();
        writer.write(formatThrown(cause));
        writer.write("\n");
        throw cause;
      }
    }
  }

  private void writeDepth() throws IOException {
    for (int i = 0; i < depth; i++) {
      writer.write("\t");
    }
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
