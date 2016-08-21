package com.mikosik.logger;

public class Depth {
  private final ThreadLocal<Integer> depth = new ThreadLocal<Integer>() {
    protected Integer initialValue() {
      return 0;
    }
  };

  public Object invoke(Closure closure) throws Throwable {
    try {
      depth.set(depth.get() + 1);
      return closure.invoke();
    } finally {
      depth.set(depth.get() - 1);
    }
  }

  public int get() {
    return depth.get();
  }

  public interface Closure {
    Object invoke() throws Throwable;
  }
}
