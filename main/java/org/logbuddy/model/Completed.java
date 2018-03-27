package org.logbuddy.model;

import static java.lang.String.format;
import static java.util.Objects.hash;
import static org.logbuddy.LogBuddyException.check;

import java.util.Objects;

public class Completed {
  public static Completed returned(Object object) {
    return new ReturnedObject(object);
  }

  public static Completed returned() {
    return new ReturnedVoid();
  }

  public static Completed thrown(Throwable throwable) {
    return new Thrown(throwable);
  }

  public static class ReturnedObject extends Completed {
    public final Object object;

    private ReturnedObject(Object object) {
      this.object = object;
    }

    public boolean equals(Object object) {
      return object instanceof ReturnedObject && equals((ReturnedObject) object);
    }

    private boolean equals(ReturnedObject returned) {
      return Objects.equals(object, returned.object);
    }

    public int hashCode() {
      return hash(object);
    }

    public String toString() {
      return format("returned(%s)", object);
    }
  }

  public static class ReturnedVoid extends Completed {
    private ReturnedVoid() {}

    public boolean equals(Object object) {
      return object instanceof ReturnedVoid;
    }

    public int hashCode() {
      return ReturnedVoid.class.hashCode();
    }

    public String toString() {
      return "returned()";
    }
  }

  public static class Thrown extends Completed {
    public final Throwable throwable;

    private Thrown(Throwable throwable) {
      check(throwable != null);
      this.throwable = throwable;
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
}
