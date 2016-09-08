package org.logbuddy.model;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static org.logbuddy.LogBuddyException.check;

import java.util.Objects;

public class Thrown {
  public final Throwable throwable;

  private Thrown(Throwable object) {
    this.throwable = object;
  }

  public static Thrown thrown(Throwable throwable) {
    check(throwable != null);
    return new Thrown(throwable);
  }

  public boolean equals(Object object) {
    return object instanceof Thrown && equals((Thrown) object);
  }

  private boolean equals(Thrown thrown) {
    return Objects.equals(throwable, thrown.throwable);
  }

  public int hashCode() {
    return hash(throwable);
  }

  public String toString() {
    return format("thrown(%s)", throwable);
  }
}
