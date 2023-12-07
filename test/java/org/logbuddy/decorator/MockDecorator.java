package org.logbuddy.decorator;

import static java.util.function.Function.identity;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

import org.logbuddy.Decorator;

public class MockDecorator implements Decorator {
  private final Map<Object, Object> stubbings;
  private final Function<Object, Object> defaultStubbing;
  private final String name;

  private MockDecorator(
      Map<Object, Object> stubbings,
      Function<Object, Object> defaultStubbing,
      String name) {
    this.stubbings = stubbings;
    this.defaultStubbing = defaultStubbing;
    this.name = name;
  }

  public static MockDecorator mockDecorator() {
    return new MockDecorator(
        new IdentityHashMap<>(),
        (argument) -> {
          throw new RuntimeException("unstubbed");
        },
        null);
  }

  public MockDecorator stub(Object argument, Object result) {
    Map<Object, Object> map = new IdentityHashMap<>(this.stubbings);
    map.put(argument, result);
    return new MockDecorator(map, defaultStubbing, name);
  }

  public MockDecorator nice() {
    return new MockDecorator(stubbings, identity(), name);
  }

  public MockDecorator name(String name) {
    return new MockDecorator(stubbings, defaultStubbing, name);
  }

  public <T> T decorate(T decorable) {
    if (stubbings.containsKey(decorable)) {
      return (T) stubbings.get(decorable);
    } else {
      return (T) defaultStubbing.apply(decorable);
    }
  }

  public String toString() {
    if (name != null) {
      return name;
    }
    throw new RuntimeException("unstubbed");
  }
}
