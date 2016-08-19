package com.mikosik.logger;

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
          writer.write(original.toString());
          writer.write(method.getName());
          for (Object argument : args) {
            writer.write(argument.toString());
          }
          writer.write("\n");
        }
        try {
          Object result = method.invoke(original, args);
          // log result
          if (predicate.test(method)) {
            writer.write(result.toString());
            writer.write("\n");
          }
          return result;
        } catch (InvocationTargetException e) {
          Throwable cause = e.getCause();
          writer.write(cause.toString());
          throw cause;
        }
      }
    };
  }
}
