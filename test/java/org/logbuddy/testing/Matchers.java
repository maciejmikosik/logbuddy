package org.logbuddy.testing;

import static java.lang.String.format;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public class Matchers {
  public static Matcher<Throwable> causedBy(Matcher<Throwable> causeMatcher) {
    return new TypeSafeMatcher<Throwable>() {
      public void describeTo(Description description) {
        description.appendText(format("causedBy(%s)", causeMatcher));
      }

      protected boolean matchesSafely(Throwable item) {
        return item != null && causeMatcher.matches(item.getCause());
      }
    };
  }
}
