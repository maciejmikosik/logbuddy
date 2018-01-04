package org.logbuddy.decorator;

import static java.lang.String.format;
import static org.logbuddy.LogBuddyException.check;

import java.util.concurrent.ConcurrentHashMap;

import org.logbuddy.Decorator;

public class CachingDecorator implements Decorator {
  private final Decorator decorator;
  private final ConcurrentHashMap<Object, Object> cache = new ConcurrentHashMap<>();

  private CachingDecorator(Decorator decorator) {
    this.decorator = decorator;
  }

  public static Decorator caching(Decorator decorator) {
    check(decorator != null);
    return new CachingDecorator(decorator);
  }

  public <T> T decorate(T decorable) {
    return (T) cache.computeIfAbsent(decorable, decorator::decorate);
  }

  public String toString() {
    return format("caching(%s)", decorator);
  }
}
