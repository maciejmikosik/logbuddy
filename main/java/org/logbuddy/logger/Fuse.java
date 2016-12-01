package org.logbuddy.logger;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static org.logbuddy.LogBuddyException.check;

import org.logbuddy.Logger;

public class Fuse {
  private final ThreadLocal<Boolean> isEnabled = ThreadLocal.withInitial(() -> true);

  private Fuse() {}

  public static Fuse fuse() {
    return new Fuse();
  }

  public Logger install(Logger logger) {
    check(logger != null);
    return new Logger() {
      public void log(Object model) {
        if (isEnabled.get()) {
          isEnabled.set(false);
          logger.log(model);
          isEnabled.set(true);
        }
      }

      public String toString() {
        return format("%s.install(%s)", Fuse.this.toString(), logger);
      }
    };
  }

  public String toString() {
    return format("fuse(%08x)", identityHashCode(this));
  }
}
