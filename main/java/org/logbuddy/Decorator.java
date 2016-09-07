package org.logbuddy;

public interface Decorator {
  public <T> T decorate(T decorable);
}
