package org.logbuddy.bind;

import static org.logbuddy.Message.message;
import static org.logbuddy.bind.StdioBinder.stdioBinder;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.PrintStream;
import java.nio.charset.Charset;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.testing.RestoreStandardStreams;

public class TestStdioBinder {
  @Rule
  public final RestoreStandardStreams restore = new RestoreStandardStreams();

  private Logger logger;
  private StdioBinder binder;
  private Charset charset;
  private String content;
  private PrintStream stdout, stderr;

  @Before
  public void before() {
    givenTest(this);
    given(charset = Charset.forName("utf8"));
    given(binder = stdioBinder(charset, logger));
  }

  @Test
  public void binds_stdout() {
    given(binder = stdioBinder(charset, logger));
    given(binder::bind);
    when(() -> System.out.println(content));
    thenCalled(logger).log(message("[stdout] " + content));
  }

  @Test
  public void binds_stderr() {
    given(binder = stdioBinder(charset, logger));
    given(binder::bind);
    when(() -> System.err.println(content));
    thenCalled(logger).log(message("[stderr] " + content));
  }

  @Test
  public void unbinds() {
    given(stdout = System.out);
    given(stderr = System.err);
    given(binder::bind);
    when(() -> binder.unbind());
    thenReturned();
    thenEqual(System.out, stdout);
    thenEqual(System.err, stderr);
  }

  @Test
  public void charset_cannot_be_null() {
    given(charset = null);
    when(() -> stdioBinder(charset, logger));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void logger_cannot_be_null() {
    given(logger = null);
    when(() -> stdioBinder(charset, logger));
    thenThrown(LogBuddyException.class);
  }
}
