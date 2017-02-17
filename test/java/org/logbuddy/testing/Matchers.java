package org.logbuddy.testing;

import static java.lang.String.format;
import static org.hamcrest.Matchers.instanceOf;
import static org.testory.Testory.any;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.logbuddy.Message;

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

  public static <T> T anyInstanceOf(Class<T> type) {
    return any(type, instanceOf(type));
  }

  public static Matcher<Message> withContent(Matcher<?> contentMatcher) {
    return new TypeSafeMatcher<Message>() {
      public void describeTo(Description description) {
        description.appendText(format("messageWithContent(%s)", contentMatcher));
      }

      protected boolean matchesSafely(Message item) {
        return contentMatcher.matches(item.content());
      }
    };
  }
}
