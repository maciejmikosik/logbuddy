package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static java.util.Collections.newSetFromMap;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.Classes.hierarchy;
import static org.logbuddy.common.Collections.removeAny;
import static org.logbuddy.common.Collections.stream;
import static org.logbuddy.common.Fields.read;
import static org.logbuddy.common.Fields.set;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.function.Predicate;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;
import org.logbuddy.Renderer;

public class TraversingDecorator implements Decorator {
  private final Decorator decorator;
  private final Predicate<Field> filter;

  private TraversingDecorator(Predicate<Field> filter, Decorator decorator) {
    this.filter = filter;
    this.decorator = decorator;
  }

  public static TraversingDecorator traversing(Decorator decorator) {
    check(decorator != null);
    return new TraversingDecorator(field -> true, decorator);
  }

  public TraversingDecorator filter(Predicate<Field> filter) {
    check(filter != null);
    return new TraversingDecorator(this.filter.and(filter), decorator);
  }

  public <T> T decorate(T startingDecorable) {
    check(startingDecorable != null);
    Set<Object> decorateds = newSetFromMap(new IdentityHashMap<>());
    Set<Object> decorables = newSetFromMap(new IdentityHashMap<>());
    decorables.add(startingDecorable);
    while (!decorables.isEmpty()) {
      Object decorable = removeAny(decorables);
      decorateds.add(decorable);

      stream(hierarchy(decorable.getClass()))
          .flatMap(type -> stream(type.getDeclaredFields()))
          .filter(field -> !field.getType().isPrimitive())
          .filter(field -> !Modifier.isStatic(field.getModifiers()))
          .filter(field -> !field.isSynthetic())
          .filter(field -> !isFromLogBuddy(field.getType()))
          .filter(filter)
          .forEach(field -> {
            Object value = read(decorable, field);
            if (value != null) {
              if (!decorateds.contains(value)) {
                decorables.add(value);
              }
              set(decorable, field, decorator.decorate(value));
            }
          });
    }
    return decorator.decorate(startingDecorable);
  }

  private static boolean isFromLogBuddy(Class<?> type) {
    return type == Logger.class
        || type == Decorator.class
        || type == Renderer.class;
  }

  public String toString() {
    return format("traversing(%s)", decorator);
  }
}
