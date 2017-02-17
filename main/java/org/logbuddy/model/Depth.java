package org.logbuddy.model;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static org.logbuddy.LogBuddyException.check;

public class Depth {
  public final int value;

  private Depth(int value) {
    this.value = value;
  }

  public static Depth depth(int value) {
    check(value >= 0);
    return new Depth(value);
  }

  public boolean equals(Object object) {
    return object instanceof Depth && equals((Depth) object);
  }

  private boolean equals(Depth depth) {
    return value == depth.value;
  }

  public int hashCode() {
    return hash(value);
  }

  public String toString() {
    return format("depth(%s)", value);
  }
}
