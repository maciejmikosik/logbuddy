package org.logbuddy.decorator;

import static java.lang.String.format;
import static org.logbuddy.decorator.CachingDecorator.caching;
import static org.logbuddy.decorator.MockDecorator.mockDecorator;
import static org.logbuddy.testing.QuackeryHelpers.assertSame;
import static org.quackery.Case.newCase;
import static org.quackery.report.AssumeException.assume;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.logbuddy.Decorator;
import org.logbuddy.LogBuddyException;
import org.quackery.Quackery;
import org.quackery.junit.QuackeryRunner;

@RunWith(QuackeryRunner.class)
public class TestCachingDecorator {
  private Object original, otherOriginal, decorated, otherDecorated;
  private Decorator decorator, cachingDecorator;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void delegates_decoration() {
    given(cachingDecorator = caching(decorator));
    given(willReturn(decorated), decorator).decorate(original);
    when(cachingDecorator.decorate(original));
    thenReturned(decorated);
  }

  @Test
  public void caches_decorated() {
    given(cachingDecorator = caching(decorator));
    given(willReturn(decorated), decorator).decorate(original);
    given(cachingDecorator.decorate(original));
    when(cachingDecorator.decorate(original));
    thenReturned(decorated);
    thenCalledTimes(1, decorator).decorate(original);
  }

  @Test
  public void cache_is_per_instance() {
    given(cachingDecorator = caching(decorator));
    given(willReturn(decorated), decorator).decorate(original);
    given(willReturn(otherDecorated), decorator).decorate(otherOriginal);
    given(cachingDecorator.decorate(original));
    when(cachingDecorator.decorate(otherOriginal));
    thenReturned(otherDecorated);
  }

  @Quackery
  public static org.quackery.Test testCachingDecorator() {
    return newCase("cache uses instance indentity, not equals", () -> {
      String undecoratedA = new String("undecorated");
      String undecoratedB = new String("undecorated");
      String decoratedA = new String("decoratedA");
      String decoratedB = new String("decoratedB");
      assume(undecoratedA != undecoratedB);
      assume(undecoratedA.equals(undecoratedB));
      Decorator decorator = mockDecorator()
          .nice()
          .stub(undecoratedA, decoratedA)
          .stub(undecoratedB, decoratedB);

      Decorator cachingDecorator = caching(decorator);
      cachingDecorator.decorate(undecoratedA);
      assertSame(
          cachingDecorator.decorate(undecoratedB),
          decoratedB);
    });
  }

  @Test
  public void implements_to_string() {
    given(cachingDecorator = caching(decorator));
    when(cachingDecorator.toString());
    thenReturned(format("caching(%s)", decorator));
  }

  @Test
  public void decorator_cannot_be_null() {
    when(() -> caching(null));
    thenThrown(LogBuddyException.class);
  }
}
