package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static org.logbuddy.LogBuddyException.check;

import java.util.Objects;

public class Text {
  public final String string;

  private Text(String string) {
    this.string = string;
  }

  public static Text text(String string) {
    check(string != null);
    return new Text(string);
  }

  public boolean equals(Object object) {
    return object instanceof Text && equals((Text) object);
  }

  private boolean equals(Text object) {
    return Objects.equals(string, object.string);
  }

  public int hashCode() {
    return hash(string);
  }

  public String toString() {
    return format("text(%s)", string);
  }
}
