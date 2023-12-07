package org.logbuddy.testing;

import static org.quackery.report.AssertException.assertTrue;

public class QuackeryHelpers {
  // TODO implement in quackery
  public static void assertSame(Object actual, Object expected) {
    assertTrue(actual == expected);
  }
}
