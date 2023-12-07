package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.Classes.hierarchy;
import static org.logbuddy.common.Collections.stream;
import static org.logbuddy.common.Fields.read;
import static org.logbuddy.common.Fields.set;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;

import org.logbuddy.Decorator;

public class ComponentsDecorator implements Decorator {
  private final Decorator decorator;

  private ComponentsDecorator(Decorator decorator) {
    this.decorator = decorator;
  }

  public static ComponentsDecorator components(Decorator decorator) {
    check(decorator != null);
    return new ComponentsDecorator(decorator);
  }

  public <T> T decorate(T decorable) {
    check(decorable != null);
    return decorable.getClass().isArray()
        ? decorateElements(decorable)
        : decorateFields(decorable);
  }

  private <T> T decorateElements(T decorable) {
    int length = Array.getLength(decorable);
    for (int index = 0; index < length; index++) {
      Object undecorated = Array.get(decorable, index);
      if (undecorated != null) {
        Object decorated = decorator.decorate(undecorated);
        Array.set(decorable, index, decorated);
      }
    }
    return decorable;
  }

  private <T> T decorateFields(T decorable) {
    stream(hierarchy(decorable.getClass()))
        .flatMap(type -> stream(type.getDeclaredFields()))
        .filter(field -> !Modifier.isStatic(field.getModifiers()))
        .forEach(field -> {
          Object value = read(decorable, field);
          if (value != null) {
            set(decorable, field, decorator.decorate(value));
          }
        });
    return decorable;
  }

  public String toString() {
    return format("components(%s)", decorator);
  }
}
