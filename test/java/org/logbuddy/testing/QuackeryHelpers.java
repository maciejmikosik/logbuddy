package org.logbuddy.testing;

import static org.quackery.help.Helpers.traverseBodies;
import static org.quackery.report.AssertException.assertTrue;

import java.lang.reflect.InaccessibleObjectException;

import org.quackery.Test;
import org.quackery.report.AssumeException;

// TODO implement in quackery
public class QuackeryHelpers {
  public static void assertSame(Object actual, Object expected) {
    assertTrue(actual == expected);
  }

  public static Test assumeAccess(Test test) {
    return traverseBodies(test, body -> () -> {
      try {
        body.run();
      } catch (InaccessibleObjectException e) {
        throw new AssumeException(e);
      }
    });
  }
}
