package org.logbuddy.logger;

import static java.lang.String.format;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.logbuddy.logger.HtmlWritingLogger.writing;
import static org.logbuddy.renderer.Html.html;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.spy;
import static org.testory.Testory.then;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;
import org.logbuddy.Message;
import org.logbuddy.Renderer;
import org.logbuddy.renderer.Html;

public class TestHtmlWritingLogger {
  private StringWriter writer;
  private Renderer<Html> renderer;
  private Logger logger;
  private Html rendered;
  private Message message;

  @Before
  public void before() {
    givenTest(this);
    given(writer = spy(new StringWriter()));
    given(rendered = html("rendered.body"));
  }

  @Test
  public void writes_line_to_writer() {
    given(logger = writing(renderer, writer));
    given(willReturn(rendered), renderer).render(message);
    when(() -> logger.log(message));
    thenReturned();
    then(writer.toString(), containsString(rendered.body));
    then(writer.toString(), endsWith("\n"));
  }

  @Test
  public void flushes_stream() {
    given(logger = writing(renderer, writer));
    given(willReturn(rendered), renderer).render(message);
    when(() -> logger.log(message));
    thenReturned();
    thenCalled(writer).flush();
  }

  @Test
  public void implements_to_string() {
    given(logger = writing(renderer, writer));
    when(logger.toString());
    thenReturned(format("writing(%s, %s)", renderer, writer));
  }

  @Test
  public void renderer_cannot_be_null() {
    given(renderer = null);
    when(() -> writing(renderer, writer));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void writer_cannot_be_null() {
    given(writer = null);
    when(() -> writing(renderer, writer));
    thenThrown(LogBuddyException.class);
  }
}
