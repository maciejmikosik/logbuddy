package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static org.logbuddy.LogBuddyException.check;

import java.util.Objects;

public class Html {
  public final String body;

  private Html(String body) {
    this.body = body;
  }

  public static Html html(String body) {
    check(body != null);
    return new Html(body);
  }

  public boolean equals(Object object) {
    return object instanceof Html && equals((Html) object);
  }

  private boolean equals(Html html) {
    return Objects.equals(body, html.body);
  }

  public int hashCode() {
    return hash(body);
  }

  public String toString() {
    return format("html(%s)", body);
  }
}
