package org.logbuddy.model;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static org.logbuddy.LogBuddyException.check;

public class InvocationDepth {
  public final int value;

  private InvocationDepth(int value) {
    this.value = value;
  }

  public static InvocationDepth invocationDepth(int value) {
    check(value >= 0);
    return new InvocationDepth(value);
  }

  public boolean equals(Object object) {
    return object instanceof InvocationDepth && equals((InvocationDepth) object);
  }

  private boolean equals(InvocationDepth depth) {
    return value == depth.value;
  }

  public int hashCode() {
    return hash(value);
  }

  public String toString() {
    return format("invocationDepth(%s)", value);
  }
}
