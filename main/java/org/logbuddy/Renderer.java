package org.logbuddy;

public interface Renderer<T> {
  public T render(Object model);
}
