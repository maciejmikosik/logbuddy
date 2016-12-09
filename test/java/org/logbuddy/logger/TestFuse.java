package org.logbuddy.logger;

import static java.lang.String.format;
import static java.lang.System.identityHashCode;
import static org.hamcrest.Matchers.not;
import static org.logbuddy.logger.Fuse.fuse;
import static org.testory.Testory.any;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.givenTry;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledNever;
import static org.testory.Testory.thenCalledTimes;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willThrow;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;

public class TestFuse {
  private Fuse fuse, otherFuse;
  private Logger logger, fused;
  private Object model;
  private Throwable throwable;

  @Before
  public void before() {
    givenTest(this);
    given(fuse = fuse());
    given(otherFuse = fuse());
    given(throwable = new RuntimeException());
  }

  @Test
  public void delegates_logging() {
    given(fused = fuse.install(logger));
    when(() -> fused.log(model));
    thenReturned();
    thenCalled(logger).log(model);
  }

  @Test
  public void ignores_recursive_invocation() {
    given(fused = fuse.install(logger));
    given(invocation -> {
      fused.log(invocation.arguments.get(0));
      return null;
    }, logger).log(any(Object.class));
    when(() -> fused.log(model));
    thenReturned();
    thenCalledTimes(1, logger).log(any(Object.class));
  }

  @Test
  public void cannot_pass_through_same_fuse_twice() {
    given(fused = fuse.install(fuse.install(logger)));
    when(() -> fused.log(model));
    thenCalledNever(logger).log(any(Object.class));
  }

  @Test
  public void fuses_are_independent() {
    given(fused = fuse.install(otherFuse.install(logger)));
    when(() -> fused.log(model));
    thenCalled(logger).log(model);
  }

  @Test
  public void recovers_from_exception() {
    given(fused = fuse.install(logger));
    given(willThrow(throwable), logger).log(any(Object.class));
    givenTry(fused).log(model);
    when(() -> fused.log(model));
    thenThrown(throwable);
    thenCalledTimes(2, logger).log(model);
  }

  @Test
  public void implements_to_string() {
    given(fuse = fuse());
    when(fuse.toString());
    thenReturned(format("fuse(%s)", format("%08x", identityHashCode(fuse))));
  }

  @Test
  public void different_fuses_have_different_id() {
    given(fuse = fuse());
    when(fuse.toString());
    thenReturned(not(fuse().toString()));
  }

  @Test
  public void installation_implements_to_string() {
    given(fused = fuse.install(logger));
    when(fused.toString());
    thenReturned(format("%s.install(%s)", fuse, logger));
  }

  @Test
  public void checks_null() {
    when(() -> fuse.install(null));
    thenThrown(LogBuddyException.class);
  }
}
