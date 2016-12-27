package org.logbuddy.decorator;

import static java.lang.String.format;
import static java.util.Arrays.stream;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.common.Classes.hierarchy;
import static org.logbuddy.common.Collections.stream;
import static org.logbuddy.common.Fields.set;

import org.logbuddy.Decorator;

public class InjectingDecoratorDecorator implements Decorator {
  private final Decorator injectable;

  private InjectingDecoratorDecorator(Decorator injectable) {
    this.injectable = injectable;
  }

  public static Decorator injecting(Decorator injectable) {
    check(injectable != null);
    return new InjectingDecoratorDecorator(injectable);
  }

  public <T> T decorate(T decorable) {
    check(decorable != null);
    stream(hierarchy(decorable.getClass()))
        .flatMap(type -> stream(type.getDeclaredFields()))
        .filter(field -> field.getType() == Decorator.class)
        .forEach(field -> set(decorable, field, injectable));
    return decorable;
  }

  public String toString() {
    return format("injecting(%s)", injectable);
  }
}
