package org.logbuddy.message;

import static java.lang.String.format;
import static java.util.Objects.hash;

import java.util.Objects;

public class Attribute {
  public final Object model;

  private Attribute(Object model) {
    this.model = model;
  }

  public static Attribute attribute(Object model) {
    return new Attribute(model);
  }

  public boolean equals(Object object) {
    return object instanceof Attribute && equals((Attribute) object);
  }

  private boolean equals(Attribute attribute) {
    return Objects.equals(model, attribute.model);
  }

  public int hashCode() {
    return hash(model);
  }

  public String toString() {
    return format("attribute(%s)", model);
  }
}
