package com.mikosik.logbuddy;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.mikosik.logbuddy.formatter.DefaultFormatter;

public class TestDecoratorWithDefaultFormatter {
  private Logger logger;
  private Formatter formatter;
  private Object argumentA, argumentB, argumentC, field;
  private Throwable throwable;
  private Decorable instance;
  private String string;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new Throwable());
    given(formatter = new DefaultFormatter());
  }

  @Test
  public void returns_from_method() {
    when(new Decorator(logger, formatter)
        .decorate(new Decorable(field))
        .methodReturningField());
    thenReturned(field);
  }

  @Test
  public void returns_from_typed_method() {
    when(new Decorator(logger, formatter)
        .decorate(new Decorable())
        .methodReturningString(string));
    thenReturned(string);
  }

  @Test
  public void throws_from_method() {
    when(() -> new Decorator(logger, formatter)
        .decorate(new Decorable(throwable))
        .methodThrowingField());
    thenThrown(throwable);
  }

  @Test
  public void logs_method_name() throws IOException {
    when(() -> new Decorator(logger, formatter)
        .decorate(new Decorable())
        .method());
    thenCalled(logger).log(any(String.class, containsString("method")));
  }

  @Test
  public void logs_arguments() throws IOException {
    when(() -> new Decorator(logger, formatter)
        .decorate(new Decorable())
        .methodWithArguments(argumentA, argumentB, argumentC));
    thenCalled(logger).log(any(String.class, containsString(argumentA.toString())));
    thenCalled(logger).log(any(String.class, containsString(argumentB.toString())));
    thenCalled(logger).log(any(String.class, containsString(argumentC.toString())));
  }

  @Test
  public void logs_instance() throws IOException {
    given(instance = new Decorable());
    when(() -> new Decorator(logger, formatter)
        .decorate(instance)
        .method());
    thenCalled(logger).log(any(String.class, containsString(instance.toString())));
  }

  @Test
  public void logs_returned_result() throws IOException {
    when(() -> new Decorator(logger, formatter)
        .decorate(new Decorable(field))
        .methodReturningField());
    thenCalled(logger).log(any(String.class, containsString("returned " + field.toString())));
  }

  @Test
  public void logs_thrown_exception() throws IOException {
    given(field = new RuntimeException());
    when(() -> new Decorator(logger, formatter)
        .decorate(new Decorable(field))
        .methodThrowingField());
    thenCalled(logger).log(any(String.class, containsString("thrown " + field.toString())));
  }

  @Test
  public void formats_invocation() throws IOException {
    when(() -> new Decorator(logger, formatter)
        .decorate(new Decorable())
        .methodWithArguments(argumentA, argumentB, argumentC));
    thenCalled(logger).log(any(String.class, stringContainsInOrder(asList(".", "(", ",", ",", ")"))));
  }

  public static class Decorable {
    private Object field;

    public Decorable() {}

    public Decorable(Object field) {
      this.field = field;
    }

    public void method() {}

    public void methodWithArguments(Object a, Object b, Object c) {}

    public Object methodReturningField() {
      return field;
    }

    public String methodReturningString(String string) {
      return string;
    }

    public Object methodThrowingField() throws Throwable {
      throw (Throwable) field;
    }
  }
}
