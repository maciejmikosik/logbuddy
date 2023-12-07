package org.logbuddy.decorator;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.logbuddy.decorator.DefaultDecomposer.decomposer;
import static org.logbuddy.testing.QuackeryHelpers.assumeAccess;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.quackery.run.Runners.expect;

import org.junit.runner.RunWith;
import org.logbuddy.LogBuddyException;
import org.quackery.Quackery;
import org.quackery.Test;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TestDefaultDecomposer {
  @Quackery
  public static Test testDefaultDecomposer() {
    return suite("test default decomposer")
        .add(suite("decomposes object")
            .add(suite("includes fields of different types")
                .add(includesObjectField())
                .add(includesPrimitiveField())
                .add(includesPrimitiveWrapperField())
                .add(includesObjectArrayField())
                .add(includesPrimitiveArrayField()))
            .add(suite("includes fields with different modifiers")
                .add(includesPriviteField())
                .add(includesPublicField())
                .add(includesFinalField()))
            .add(suite("includes fields from super classes")
                .add(includesFieldFromSuperclass())
                .add(includesFieldFromSuperSuperclass())
                .add(fieldsFromSubclassesAreFirst()))
            .add(suite("includes fields from super classes")
                .add(includesFieldFromSuperclass())
                .add(includesFieldFromSuperSuperclass())
                .add(fieldsFromSubclassesAreFirst()))
            .add(includesReferenceToOuterClass())
            .add(excludesStaticField())
            .add(excludesNullField())
            .add(excludesNullElement())
            .add(keepsOrderOfFields())
            .add(assumeAccess(decomposesPrimitiveWrapper())))
        .add(suite("decomposes arrays")
            .add(suite("of different lengths")
                .add(decomposesEmptyArray())
                .add(decomposesSingletonArray())
                .add(decomposesMultiArray()))
            .add(suite("of different types")
                .add(decomposesStringArray())
                .add(decomposesPrimitiveArray()))
            .add(decomposesMultidimensionalArrayOnce()))
        .add(implementsToString())
        .add(validatesArguments());
  }

  private static Test includesObjectField() {
    return newCase("object", () -> {
      class Composite {
        @SuppressWarnings("unused")
        Object component;
      }
      Composite composite = new Composite();
      Object component = new Object();
      composite.component = component;

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test includesPrimitiveField() {
    return newCase("primitive", () -> {
      class Composite {
        @SuppressWarnings("unused")
        int component;
      }
      Composite composite = new Composite();
      composite.component = 123;

      assertEquals(
          decomposer().decompose(composite),
          singletonList(123));
    });
  }

  private static Test includesPrimitiveWrapperField() {
    return newCase("primitive wrapper", () -> {
      class Composite {
        @SuppressWarnings("unused")
        Integer component;
      }
      Composite composite = new Composite();
      Integer component = 123;
      composite.component = component;

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test includesObjectArrayField() {
    return newCase("object array", () -> {
      class Composite {
        @SuppressWarnings("unused")
        Object[] component;
      }
      Composite composite = new Composite();
      Object[] component = new Object[] { new Object() };
      composite.component = component;

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test includesPrimitiveArrayField() {
    return newCase("primitive array", () -> {
      class Composite {
        @SuppressWarnings("unused")
        int[] component;
      }
      Composite composite = new Composite();
      int[] component = new int[] { 123 };
      composite.component = component;

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test includesPriviteField() {
    return newCase("private", () -> {
      class Composite {
        @SuppressWarnings("unused")
        private Object component;
      }
      Composite composite = new Composite();
      Object component = new Object();
      composite.component = component;

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test includesPublicField() {
    return newCase("public", () -> {
      class Composite {
        @SuppressWarnings("unused")
        public Object component;
      }
      Composite composite = new Composite();
      Object component = new Object();
      composite.component = component;

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test includesFinalField() {
    return newCase("final", () -> {
      Object component = new Object();
      class Composite {
        @SuppressWarnings("unused")
        final Object fElement = component;
      }
      Composite composite = new Composite();

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test includesFieldFromSuperclass() {
    return newCase("from superclass", () -> {
      class SuperComposite {
        @SuppressWarnings("unused")
        public Object component;
      }
      class Composite extends SuperComposite {}
      Composite composite = new Composite();
      Object component = new Object();
      composite.component = component;

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test includesFieldFromSuperSuperclass() {
    return newCase("from super superclass", () -> {
      class SuperSuperComposite {
        @SuppressWarnings("unused")
        public Object component;
      }
      class SuperComposite extends SuperSuperComposite {}
      class Composite extends SuperComposite {}
      Composite composite = new Composite();
      Object component = new Object();
      composite.component = component;

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test fieldsFromSubclassesAreFirst() {
    return newCase("fields from subclasses are first", () -> {
      class SuperComposite {
        @SuppressWarnings("unused")
        public Object superElement;
      }
      class Composite extends SuperComposite {
        @SuppressWarnings("unused")
        public Object component;
      }
      Composite composite = new Composite();
      Object component = new Object();
      Object superElement = new Object();
      composite.component = component;
      composite.superElement = superElement;

      assertEquals(
          decomposer().decompose(composite),
          asList(component, superElement));
    });
  }

  private static Test includesReferenceToOuterClass() {
    class Outer {
      Inner newInner() {
        return new Inner();
      }

      class Inner {}
    }

    return newCase("includes reference to outer class", () -> {
      Outer outer = new Outer();
      Outer.Inner inner = outer.newInner();
      assertEquals(
          decomposer().decompose(inner),
          singletonList(outer));
    });
  }

  static class StaticComposite {
    static Object component;
  }

  private static Test excludesStaticField() {
    return newCase("excludes static fields", () -> {
      StaticComposite composite = new StaticComposite();
      Object component = new Object();
      StaticComposite.component = component;

      assertEquals(
          decomposer().decompose(composite),
          emptyList());
    });
  }

  private static Test excludesNullField() {
    return newCase("excludes null fields", () -> {
      class Composite {
        @SuppressWarnings("unused")
        Object component;
      }
      Composite composite = new Composite();
      composite.component = null;

      assertEquals(
          decomposer().decompose(composite),
          emptyList());
    });
  }

  private static Test excludesNullElement() {
    return newCase("excludes null elements", () -> {
      Object[] composite = new Object[] { null };

      assertEquals(
          decomposer().decompose(composite),
          emptyList());
    });
  }

  private static Test keepsOrderOfFields() {
    return newCase("keeps order of fields", () -> {
      @SuppressWarnings("unused")
      class Composite {
        public Object componentA;
        public Object componentB;
        public Object componentC;
      }
      Composite composite = new Composite();
      Object componentA = new Object();
      Object componentB = new Object();
      Object componentC = new Object();
      composite.componentA = componentA;
      composite.componentB = componentB;
      composite.componentC = componentC;

      assertEquals(
          decomposer().decompose(composite),
          asList(componentA, componentB, componentC));
    });
  }

  private static Test decomposesPrimitiveWrapper() {
    return newCase("decomposes primitive wrapper", () -> {
      assertEquals(
          decomposer().decompose(Integer.valueOf(123)),
          singletonList(123));
    });
  }

  private static Test decomposesEmptyArray() {
    return newCase("empty array", () -> {
      Object[] composite = new Object[] {};
      assertEquals(
          decomposer().decompose(composite),
          emptyList());
    });
  }

  private static Test decomposesSingletonArray() {
    return newCase("array with 1 element", () -> {
      Object component = new Object();
      Object[] composite = new Object[] { component };

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test decomposesMultiArray() {
    return newCase("array with 3 elements", () -> {
      Object componentA = new Object();
      Object componentB = new Object();
      Object componentC = new Object();
      Object composite = new Object[] { componentA, componentB, componentC };

      assertEquals(
          decomposer().decompose(composite),
          asList(componentA, componentB, componentC));
    });
  }

  private static Test decomposesStringArray() {
    return newCase("string array", () -> {
      String component = "string";
      Object composite = new String[] { component };

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test decomposesPrimitiveArray() {
    return newCase("primitive array", () -> {
      int component = 123;
      Object composite = new int[] { component };

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test decomposesMultidimensionalArrayOnce() {
    return newCase("decomposes multidimension array once", () -> {
      Object[] component = new Object[] { new Object() };
      Object composite = new Object[] { component };

      assertEquals(
          decomposer().decompose(composite),
          singletonList(component));
    });
  }

  private static Test implementsToString() {
    return newCase("implements toString()", () -> {
      assertEquals(
          decomposer().toString(),
          "decomposer()");
    });
  }

  private static Test validatesArguments() {
    return expect(LogBuddyException.class,
        newCase("composite cannot be null", () -> {
          decomposer().decompose(null);
        }));
  }
}
