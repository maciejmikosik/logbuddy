package com.mikosik.logger;

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

public class TestLogging {
  private Logger logger;
  private Object argumentA, argumentB, argumentC, field;
  private Throwable throwable;
  private Wrappable instance;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new Throwable());
  }

  @Test
  public void returns_from_method() {
    when(new Logging(logger)
        .wrap(new Wrappable(field))
        .methodReturningField());
    thenReturned(field);
  }

  @Test
  public void throws_from_method() {
    when(() -> new Logging(logger)
        .wrap(new Wrappable(throwable))
        .methodThrowingField());
    thenThrown(throwable);
  }

  @Test
  public void logs_method_name() throws IOException {
    when(() -> new Logging(logger)
        .wrap(new Wrappable())
        .method());
    thenCalled(logger).log(any(String.class, containsString("method")));
  }

  @Test
  public void logs_arguments() throws IOException {
    when(() -> new Logging(logger)
        .wrap(new Wrappable())
        .methodWithArguments(argumentA, argumentB, argumentC));
    thenCalled(logger).log(any(String.class, containsString(argumentA.toString())));
    thenCalled(logger).log(any(String.class, containsString(argumentB.toString())));
    thenCalled(logger).log(any(String.class, containsString(argumentC.toString())));
  }

  @Test
  public void logs_instance() throws IOException {
    given(instance = new Wrappable());
    when(() -> new Logging(logger)
        .wrap(instance)
        .method());
    thenCalled(logger).log(any(String.class, containsString(instance.toString())));
  }

  @Test
  public void logs_returned_result() throws IOException {
    when(() -> new Logging(logger)
        .wrap(new Wrappable(field))
        .methodReturningField());
    thenCalled(logger).log(any(String.class, containsString("returned " + field.toString())));
  }

  @Test
  public void logs_thrown_exception() throws IOException {
    given(field = new RuntimeException());
    when(() -> new Logging(logger)
        .wrap(new Wrappable(field))
        .methodThrowingField());
    thenCalled(logger).log(any(String.class, containsString("thrown " + field.toString())));
  }

  @Test
  public void formats_invocation() throws IOException {
    when(() -> new Logging(logger)
        .wrap(new Wrappable())
        .methodWithArguments(argumentA, argumentB, argumentC));
    thenCalled(logger).log(any(String.class, stringContainsInOrder(asList(".", "(", ",", ",", ")"))));
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
