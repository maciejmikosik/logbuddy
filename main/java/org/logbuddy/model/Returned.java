package org.logbuddy.model;

import static java.lang.String.format;
import static java.util.Objects.hash;

import java.util.Objects;

public class Returned {
  public final Object object;

  private Returned(Object object) {
    this.object = object;
  }

  public static Returned returned(Object object) {
    return new Returned(object);
  }

  public boolean equals(Object object) {
    return object instanceof Returned && equals((Returned) object);
  }

  private boolean equals(Returned returned) {
    return Objects.equals(object, returned.object);
  }

  public int hashCode() {
    return hash(object);
  }

  public String toString() {
    return format("returned(%s)", object);
  }
}
