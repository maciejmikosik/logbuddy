package com.mikosik.logger;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.stringContainsInOrder;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.then;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.function.Predicate;

import org.junit.Before;
import org.junit.Test;

public class TestConfiguration {
  private Predicate<Method> allMethods, noMethods;
  private StringWriter writer;
  private Object argumentA, argumentB, argumentC, field;
  private Throwable throwable;
  private Wrappable instance;
  private Configuration configuration;

  @Before
  public void before() {
    givenTest(this);
    given(writer = new StringWriter());
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
    thenEqual(writer.toString(), "");
  }

  @Test
  public void logs_method_name() throws IOException {
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable())
        .method());
    then(writer.toString(), containsString("method"));
  }

  @Test
  public void logs_arguments() throws IOException {
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable())
        .methodWithArguments(argumentA, argumentB, argumentC));
    then(writer.toString(), containsString(argumentA.toString()));
    then(writer.toString(), containsString(argumentB.toString()));
    then(writer.toString(), containsString(argumentC.toString()));
  }

  @Test
  public void logs_instance() throws IOException {
    given(instance = new Wrappable());
    when(() -> new Configuration(allMethods, writer)
        .wrap(instance)
        .method());
    then(writer.toString(), containsString(instance.toString()));
  }

  @Test
  public void logs_returned_result() throws IOException {
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable(field))
        .methodReturningField());
    then(writer.toString(), containsString("returned " + field.toString() + "\n"));
  }

  @Test
  public void logs_thrown_exception() throws IOException {
    given(field = new RuntimeException());
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable(field))
        .methodThrowingField());
    then(writer.toString(), containsString("thrown " + field.toString() + "\n"));
  }

  @Test
  public void formats_invocation() throws IOException {
    when(() -> new Configuration(allMethods, writer)
        .wrap(new Wrappable())
        .methodWithArguments(argumentA, argumentB, argumentC));
    then(writer.toString(), stringContainsInOrder(asList(".", "(", ",", ",", ")\n")));
  }

  @Test
  public void formats_stack_indentation_if_returned() {
    given(configuration = new Configuration(allMethods, writer));
    when(() -> configuration.wrap(new Wrappable(
        configuration.wrap(new Wrappable(
            configuration.wrap(new Wrappable())))))
        .chain());
    then(writer.toString(), stringContainsInOrder(asList(
        "chain", "\n",
        "\t", "chain", "\n",
        "\t\t", "chain", "\n",
        "\t\treturned", "\n",
        "\treturned", "\n",
        "returned", "\n")));
  }

  @Test
  public void formats_stack_indentation_if_thrown() {
    given(configuration = new Configuration(allMethods, writer));
    when(() -> configuration.wrap(new Wrappable(
        configuration.wrap(new Wrappable(
            configuration.wrap(new Wrappable(throwable))))))
        .chain());
    then(writer.toString(), stringContainsInOrder(asList(
        "chain", "\n",
        "\t", "chain", "\n",
        "\t\t", "chain", "\n",
        "\t\tthrown", "\n",
        "\tthrown", "\n",
        "thrown", "\n")));
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

    public void chain() throws Throwable {
      if (field instanceof Wrappable) {
        ((Wrappable) field).chain();
      } else if (field instanceof Throwable) {
        throw (Throwable) field;
      }
    }
  }
}
