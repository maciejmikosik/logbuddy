package org.logbuddy.logger;

import static java.lang.String.format;
import static org.hamcrest.Matchers.sameInstance;
import static org.logbuddy.logger.WritingLogger.logger;
import static org.logbuddy.testing.Matchers.causedBy;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.onInstance;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;
import static org.testory.Testory.willThrow;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.Renderer;

public class TestWritingLogger {
  private Writer writer;
  private Renderer<String> renderer;
  private Logger logger;
  private String rendered;
  private Message message;
  private IOException ioException;

  @Before
  public void before() {
    givenTest(this);
    given(rendered = "rendered.string");
    given(ioException = new IOException());
  }

  @Test
  public void writes_line_to_writer() {
    given(writer = new StringWriter());
    given(logger = logger(writer, renderer));
    given(willReturn(rendered), renderer).render(message);
    when(() -> logger.log(message));
    thenReturned();
    thenEqual(writer.toString(), rendered);
  }

  @Test
  public void flushes_stream() throws IOException {
    given(logger = logger(writer, renderer));
    given(willReturn(rendered), renderer).render(message);
    when(() -> logger.log(message));
    thenReturned();
    thenCalled(writer).flush();
  }

  @Test
  public void wraps_io_exception() {
    given(logger = logger(writer, renderer));
    given(willThrow(ioException), onInstance(writer));
    when(() -> logger.log(message));
    thenThrown(LogBuddyException.class);
    thenThrown(causedBy(sameInstance(ioException)));
  }

  @Test
  public void implements_to_string() {
    given(logger = logger(writer, renderer));
    when(logger.toString());
    thenReturned(format("logger(%s, %s)", writer, renderer));
  }

  @Test
  public void renderer_cannot_be_null() {
    given(renderer = null);
    when(() -> logger(writer, renderer));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void writer_cannot_be_null() {
    given(writer = null);
    when(() -> logger(writer, renderer));
    thenThrown(LogBuddyException.class);
  }
}
