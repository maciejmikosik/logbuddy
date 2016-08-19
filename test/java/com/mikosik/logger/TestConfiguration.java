package com.mikosik.logger;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledNever;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

public class TestConfiguration {
  private Predicate<Method> allMethods, noMethods;
  private Writer writer;
  private Object argumentA, argumentB, argumentC, field;
  private Throwable throwable;
  private Wrappable original;

  @Before
  public void before() {
    givenTest(this);
    given(allMethods = method -> true);
    given(noMethods = method -> false);
    given(throwable = new Throwable());
  }

  @Test
  public void returns_from_unlogged_method() {
    when(new Configuration(noMethods, writer)
        .wrap(new Wrappable(field))
        .methodReturningField());
    thenReturned(field);
  }

  @Test
  public void returns_from_logged_method() {
    when(new Configuration(allMethods, writer)
        .wrap(new Wrappable(field))
        .methodReturningField());
    thenReturned(field);
  }

  @Test
  public void throws_from_unlogged_method() {
    when(() -> new Configuration(noMethods, writer)
        .wrap(new Wrappable(throwable))
        .methodThrowingField());
    thenThrown(throwable);
  }

  @Test
  public void throws_from_logged_method() {
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable(throwable))
        .methodThrowingField());
    thenThrown(throwable);
  }

  @Test
  public void does_not_log_unmatched_method() throws IOException {
    when(() -> new Configuration(noMethods, writer)
        .wrap(new Wrappable())
        .method());
    thenCalledNever(onInstance(writer));
  }

  @Test
  public void logs_method_name() throws IOException {
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable())
        .method());
    thenCalled(writer).write(any(String.class, containsString("method")));
  }

  @Test
  public void logs_arguments() throws IOException {
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable())
        .methodWithArguments(argumentA, argumentB, argumentC));
    thenCalled(writer).write(any(String.class, containsString(argumentA.toString())));
    thenCalled(writer).write(any(String.class, containsString(argumentB.toString())));
    thenCalled(writer).write(any(String.class, containsString(argumentC.toString())));
  }

  @Test
  public void logs_instance() throws IOException {
    given(original = new Wrappable());
    when(() -> new Configuration(allMethods, writer)
        .wrap(original)
        .method());
    thenCalled(writer).write(any(String.class, containsString(original.toString())));
  }

  @Test
  public void logs_returned_result() throws IOException {
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable(field))
        .methodReturningField());
    thenCalled(writer).write(any(String.class, containsString(field.toString())));
  }

  @Test
  public void logs_thrown_exception() throws IOException {
    given(field = new RuntimeException());
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable(field))
        .methodThrowingField());
    thenCalled(writer).write(any(String.class, containsString(field.toString())));
  }

  public static class Wrappable {
    private Object field;

    public Wrappable() {}

    public Wrappable(Object field) {
      this.field = field;
    }

    public void method() {}

    public void methodWithArguments(Object a, Object b, Object c) {}

    public Object methodReturningField() {
      return field;
    }

    public Object methodThrowingField() throws Throwable {
      throw (Throwable) field;
    }
  }
}
