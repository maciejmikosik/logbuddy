package org.logbuddy.logger;

import static org.logbuddy.logger.ConsoleLogger.console;
import static org.logbuddy.renderer.Text.text;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.spy;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Renderer;
import org.logbuddy.renderer.Text;

public class TestConsoleLogger {
  private StringWriter writer;
  private Renderer<Text> renderer;
  private Logger logger;
  private Text rendered;
  private Object model;

  @Before
  public void before() {
    givenTest(this);
    given(writer = spy(new StringWriter()));
    given(rendered = text("rendered.string"));
  }

  @Test
  public void writes_line_to_writer() {
    given(logger = console(renderer, writer));
    given(willReturn(rendered), renderer).render(model);
    when(() -> logger.log(model));
    thenReturned();
    thenEqual(writer.toString(), rendered.string + "\n");
  }

  @Test
  public void flushes_stream() {
    given(logger = console(renderer, writer));
    given(willReturn(rendered), renderer).render(model);
    when(() -> logger.log(model));
    thenReturned();
    thenCalled(writer).flush();
  }

  @Test
  public void renderer_cannot_be_null() {
    given(renderer = null);
    when(() -> console(renderer, writer));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void writer_cannot_be_null() {
    given(writer = null);
    when(() -> console(renderer, writer));
    thenThrown(LogBuddyException.class);
  }
}
