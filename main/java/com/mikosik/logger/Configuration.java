package com.mikosik.logger;

import static org.testory.proxy.Invocation.invocation;
import static org.testory.proxy.Invocations.invoke;
import static org.testory.proxy.Proxies.proxy;
import static org.testory.proxy.Typing.typing;

import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.function.Predicate;

import org.testory.proxy.Handler;
import org.testory.proxy.Invocation;
import org.testory.proxy.Typing;

public class Configuration {
  private final Writer writer;
  private final Predicate<Method> predicate;

  public Configuration(Predicate<Method> predicate, Writer writer) {
    this.predicate = predicate;
    this.writer = writer;
  }

  public <T> T wrap(T original) {
    Typing typing = typing(original.getClass(), new HashSet<Class<?>>());
    return (T) proxy(typing, new Handler() {
      public Object handle(Invocation invocation) throws Throwable {
        if (predicate.test(invocation.method)) {
          writer.write(original.toString());
          writer.write(invocation.method.getName());
          for (Object argument : invocation.arguments) {
            writer.write(argument.toString());
          }
          writer.write("\n");
        }
        try {
          Object result = invoke(invocation(invocation.method, original, invocation.arguments));
          // log result
          if (predicate.test(invocation.method)) {
            writer.write(result.toString());
            writer.write("\n");
          }
          return result;
        } catch (Throwable throwable) {
          writer.write(throwable.toString());
          // log exception
          throw throwable;
        }
      }
    });
  }
}
