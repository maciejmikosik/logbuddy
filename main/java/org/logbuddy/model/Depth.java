package org.logbuddy.model;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static org.logbuddy.LogBuddyException.check;

import java.util.Objects;

public class Depth {
  public final int value;
  public final Object model;

  private Depth(int value, Object model) {
    this.value = value;
    this.model = model;
  }

  public static Depth depth(int value, Object model) {
    check(value >= 0);
    check(model != null);
    return new Depth(value, model);
  }

  public boolean equals(Object object) {
    return object instanceof Depth && equals((Depth) object);
  }

  private boolean equals(Depth depth) {
    return Objects.equals(value, depth.value)
        && Objects.equals(model, depth.model);
  }

  public int hashCode() {
    return hash(value);
  }

  public String toString() {
    return format("depth(%s, %s)", value, model);
  }
}
