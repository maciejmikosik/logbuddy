package org.logbuddy.decorator;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static org.logbuddy.decorator.JdkDecorator.jdk;
import static org.logbuddy.decorator.MockDecorator.mockDecorator;
import static org.logbuddy.decorator.PrepareDecorator.decorator;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.quackery.report.AssertException.fail;
import static org.quackery.run.Runners.expect;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;

import org.junit.runner.RunWith;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.quackery.Quackery;
import org.quackery.Test;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TestJdkDecorator {
  @Quackery
  public static Test testJdkDecorator() {
    return suite("jdk decorator")
        .add(suite("delegates decoration of")
            .add(delegatesDecoration(new Object(), new Object()))
            .add(delegatesDecoration("undecorated", "decorated"))
            .add(delegatesDecoration(
                new ArrayList<>(asList("undecorated")),
                new ArrayList<>(asList("decorated"))))
            .add(delegatesDecoration(
                new LinkedList<>(asList("undecorated")),
                new LinkedList<>(asList("decorated"))))
            .add(delegatesDecoration(
                new Object[] { "undecorated" },
                new Object[] { "decorated" }))
            .add(delegatesDecoration(
                new int[] { 123 },
                new int[] { 124 }))
            .add(delegatesDecoration(
                Integer.valueOf(123),
                Integer.valueOf(124)))
            .add(delegatesDecoration(
                new PublicClass(),
                new PublicClass()))
            .add(delegatesDecoration(
                new PackagePrivateClass(),
                new PackagePrivateClass()))
            .add(delegatesDecoration(
                new PrivateClass(),
                new PrivateClass())))
        .add(suite("peels java package-private classes")
            .add(peels(
                asList("undecorated"),
                AbstractList.class,
                RandomAccess.class,
                Serializable.class))
            .add(peels(
                unmodifiableList(asList("undecorated")),
                Object.class,
                RandomAccess.class,
                java.util.List.class,
                Collection.class,
                Serializable.class))
            .add(peels(
                unmodifiableList(new LinkedList<>(asList("undecorated"))),
                Object.class,
                java.util.List.class,
                Collection.class,
                Serializable.class))
            .add(proxyForwardsCalls())
            .add(cannotCastProxyToOriginalType()))
        .add(implementsToString())
        .add(validatesArguments());
  }

  private static Test delegatesDecoration(Object undecorated, Object decorated) {
    return newCase(undecorated.getClass().getName(), () -> {
      Decorator decorator = mockDecorator()
          .stub(undecorated, decorated);
      assertEquals(
          jdk(decorator).decorate(undecorated),
          decorated);
    });
  }

  private static Test peels(
      Object undecorated,
      Class<?> superclass,
      Class<?>... interfaces) {
    return newCase(undecorated.getClass().getName(), () -> {
      Object decorated = jdk(decorator).decorate(undecorated);
      assertEquals(decorated.getClass().getSuperclass(), superclass);
      assertEquals(decorated.getClass().getInterfaces(), interfaces);
    });
  }

  private static Test proxyForwardsCalls() {
    return newCase("proxy forwards calls", () -> {
      List<String> decorable = Arrays.asList("string");
      List<String> decorated = jdk(decorator).decorate(decorable);
      assertEquals(decorated.get(0), "string");
    });
  }

  private static Test cannotCastProxyToOriginalType() {
    return newCase("cannot cast proxy to original type", () -> {
      Class<?> nonPublicType = Arrays.asList("string").getClass();
      List<String> decorable = Arrays.asList("string");
      List<String> decorated = jdk(decorator).decorate(decorable);
      try {
        nonPublicType.cast(decorated);
        fail();
      } catch (ClassCastException e) {}
    });
  }

  private static Test implementsToString() {
    return newCase("implements toString()", () -> {
      Decorator decorator = new Decorator() {
        public <T> T decorate(T decorable) {
          return null;
        }

        public String toString() {
          return "decorator";
        }
      };
      assertEquals(jdk(decorator).toString(), "jdk(decorator)");
    });
  }

  private static Test validatesArguments() {
    return expect(LogBuddyException.class, suite("validates arguments")
        .add(newCase("decorator cannot be null", () -> {
          jdk(null);
        }))
        .add(newCase("decorable cannot be null", () -> {
          jdk(decorator).decorate(null);
        })));
  }

  private static class PrivateClass {}

  static class PackagePrivateClass {}

  public static class PublicClass {}
}
