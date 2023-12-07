package org.logbuddy.decorator;

import static java.util.Arrays.stream;
import static org.logbuddy.common.Fields.read;
import static org.logbuddy.decorator.ComponentsDecorator.components;
import static org.logbuddy.decorator.MockDecorator.mockDecorator;
import static org.logbuddy.decorator.PrepareDecorator.decorated;
import static org.logbuddy.decorator.PrepareDecorator.decorator;
import static org.logbuddy.decorator.PrepareDecorator.undecorated;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.quackery.run.Runners.expect;

import java.lang.reflect.Field;

import org.junit.runner.RunWith;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.quackery.Quackery;
import org.quackery.Test;
import org.quackery.junit.QuackeryRunner;
import org.quackery.report.AssumeException;

@RunWith(QuackeryRunner.class)
public class TestComponentsDecorator {
  @Quackery
  public static Test testComponentsDecorator() {
    return suite("components decorator")
        .add(returnsOriginalDecorable())
        .add(suite("decorates field")
            .add(suite("of type")
                .add(includesObjectField())
                .add(includesPrimitiveField())
                .add(includesPrimitiveWrapperField())
                .add(includesObjectArrayField())
                .add(includesPrimitiveArrayField()))
            .add(suite("with modifier")
                .add(includesPublicField())
                .add(includesPackagePrivateField())
                .add(includesPrivateField())
                .add(includesFinalField()))
            .add(includesFieldFromSuperclass())
            .add(includesReferenceToOuterClass()))
        .add(suite("does not decorate field")
            .add(excludesFieldAssignToNull())
            .add(excludesStaticField()))
        .add(suite("decorates array elements of type")
            .add(includesObjectElements())
            .add(includesObjectArrayElements())
            .add(includesPrimitiveElements())
            .add(includesPrimitiveArrayElements()))
        .add(implementsToString())
        .add(validatesArguments());
  }

  private static Test returnsOriginalDecorable() {
    return newCase("returns original decorable", () -> {
      class Decorable {}
      Decorable decorable = new Decorable();

      assertEquals(
          components(decorator).decorate(decorable),
          decorable);
    });
  }

  private static Test includesObjectField() {
    return newCase("Object", () -> {
      class Decorable {
        Object field;
      }
      Decorable decorable = new Decorable();
      decorable.field = undecorated;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesPrimitiveField() {
    return newCase("primitive", () -> {
      int undecorated = 123;
      int decorated = 124;
      Decorator decorator = mockDecoratorEqual(undecorated, decorated);

      class Decorable {
        int field;
      }
      Decorable decorable = new Decorable();
      decorable.field = undecorated;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesPrimitiveWrapperField() {
    return newCase("Integer", () -> {
      Integer undecorated = 123;
      Integer decorated = 124;
      Decorator decorator = mockDecoratorEqual(undecorated, decorated);

      class Decorable {
        Integer field;
      }
      Decorable decorable = new Decorable();
      decorable.field = undecorated;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesObjectArrayField() {
    return newCase("Object[]", () -> {
      Object[] undecorated = new Object[] { new Object() };
      Object[] decorated = new Object[] { new Object() };
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      class Decorable {
        Object[] field;
      }
      Decorable decorable = new Decorable();
      decorable.field = undecorated;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesPrimitiveArrayField() {
    return newCase("int[]", () -> {
      int[] undecorated = new int[] { 1 };
      int[] decorated = new int[] { 2 };
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      class Decorable {
        int[] field;
      }
      Decorable decorable = new Decorable();
      decorable.field = undecorated;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesPublicField() {
    return newCase("public", () -> {
      class Decorable {
        public Object field;
      }
      Decorable decorable = new Decorable();
      decorable.field = undecorated;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesPackagePrivateField() {
    return newCase("none (package private)", () -> {
      class Decorable {
        Object field;
      }
      Decorable decorable = new Decorable();
      decorable.field = undecorated;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesPrivateField() {
    return newCase("private", () -> {
      class Decorable {
        private Object field;
      }
      Decorable decorable = new Decorable();
      decorable.field = undecorated;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesFinalField() {
    return newCase("final", () -> {
      class Decorable {
        final Object field = undecorated;
      }
      Decorable decorable = new Decorable();

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesFieldFromSuperclass() {
    return newCase("from superclass", () -> {
      class SuperDecorable {
        Object field;
      }
      class Decorable extends SuperDecorable {}
      Decorable decorable = new Decorable();
      decorable.field = undecorated;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, decorated);
    });
  }

  private static Test includesObjectElements() {
    return newCase("Object[]", () -> {
      Object[] decorable = new Object[] { undecorated };
      components(decorator).decorate(decorable);
      assertEquals(decorable[0], decorated);
    });
  }

  private static Test includesObjectArrayElements() {
    return newCase("Object[][]", () -> {
      Object[] undecorated = new Object[] { new Object() };
      Object[] decorated = new Object[] { new Object() };
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      Object[] decorable = new Object[][] { undecorated };

      components(decorator).decorate(decorable);
      assertEquals(decorable[0], decorated);
    });
  }

  private static Test includesPrimitiveElements() {
    return newCase("int[]", () -> {
      int undecorated = 123;
      int decorated = 124;
      Decorator decorator = mockDecoratorEqual(undecorated, decorated);

      int[] decorable = new int[] { undecorated };

      components(decorator).decorate(decorable);
      assertEquals(decorable[0], decorated);
    });
  }

  private static Test includesPrimitiveArrayElements() {
    return newCase("int[][]", () -> {
      int[] undecorated = new int[] { 123 };
      int[] decorated = new int[] { 124 };
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      int[][] decorable = new int[][] { undecorated };

      components(decorator).decorate(decorable);
      assertEquals(decorable[0], decorated);
    });
  }

  private static Test excludesFieldAssignToNull() {
    return newCase("assigned to null", () -> {
      Decorator decorator = mockDecorator()
          .nice()
          .stub(null, decorated);

      class Decorable {
        Object field;
      }
      Decorable decorable = new Decorable();
      decorable.field = null;

      components(decorator).decorate(decorable);
      assertEquals(decorable.field, null);
    });
  }

  static class StaticDecorable {
    static Object field;
  }

  private static Test excludesStaticField() {
    return newCase("with modifier static", () -> {
      StaticDecorable node = new StaticDecorable();
      StaticDecorable.field = undecorated;

      components(decorator).decorate(node);
      assertEquals(StaticDecorable.field, undecorated);
    });
  }

  private static Test includesReferenceToOuterClass() {
    return newCase("reference to outer class", () -> {
      class Outer {
        Inner newInner() {
          return new Inner();
        }

        class Inner {}
      }
      Field syntheticOuter = stream(Outer.Inner.class.getDeclaredFields())
          .filter(field -> field.getType() == Outer.class)
          .findFirst().orElseThrow(AssumeException::new);

      Outer undecorated = new Outer();
      Outer decorated = new Outer();
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecorated, decorated);

      Outer.Inner inner = undecorated.newInner();

      components(decorator).decorate(inner);
      assertEquals(read(inner, syntheticOuter), decorated);
    });
  }

  private static Test implementsToString() {
    return newCase("implements toString()", () -> {
      Decorator decorator = mockDecorator()
          .name("decorator");
      assertEquals(
          components(decorator).toString(),
          "components(decorator)");
    });
  }

  private static Test validatesArguments() {
    return expect(LogBuddyException.class, suite("validates arguments")
        .add(newCase("decorator cannot be null", () -> {
          components(null);
        }))
        .add(newCase("decorable cannot be null", () -> {
          components(decorator).decorate(null);
        })));
  }

  private static Decorator mockDecoratorEqual(int undecorated, int decorated) {
    return new Decorator() {
      public <T> T decorate(T decorable) {
        return decorable.equals(undecorated)
            ? (T) Integer.valueOf(decorated)
            : decorable;
      }
    };
  }
}
