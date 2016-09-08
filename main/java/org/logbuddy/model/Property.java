package org.logbuddy.model;

import static java.lang.String.format;
import static java.util.Objects.hash;

import java.util.Objects;

public class Property {
  public final Object value;
  public final Object model;

  private Property(Object value, Object model) {
    this.value = value;
    this.model = model;
  }

  public static Property property(Object value, Object model) {
    return new Property(value, model);
  }

  public boolean equals(Object object) {
    return object instanceof Property && equals((Property) object);
  }

  private boolean equals(Property property) {
    return Objects.equals(value, property.value)
        && Objects.equals(model, property.model);
  }

  public int hashCode() {
    return hash(value, model);
  }

  public String toString() {
    return format("property(%s, %s)", value, model);
  }
}
