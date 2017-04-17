package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.logbuddy.Message.message;
import static org.logbuddy.model.Completed.returned;
import static org.logbuddy.model.Completed.thrown;
import static org.logbuddy.model.InvocationDepth.invocationDepth;
import static org.logbuddy.model.Invoked.invoked;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Renderer;

public class TestHtmlRenderer {
  private Renderer<String> htmlRenderer;
  private Object object;
  private String string;
  private Method method;
  private Object instance, a, b, c;
  private Throwable throwable;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new Throwable());
    given(htmlRenderer = new HtmlRenderer());
  }

  @Test
  public void renders_string() {
    when(htmlRenderer.render(string));
    thenReturned(string);
  }

  @Test
  public void renders_null() {
    when(htmlRenderer.render(null));
    thenReturned("null");
  }

  @Test
  public void escapes_html_characters() {
    when(htmlRenderer.render("&_<_>_ _\t_\""));
    thenReturned("&amp;_&lt;_&gt;_&nbsp;_&nbsp;&nbsp;_&quot;");
  }

  @Test
  public void renders_message() {
    given(htmlRenderer = new HtmlRenderer());
    when(htmlRenderer.render(message(object)
        .attribute(a)
        .attribute(b)));
    thenReturned(format(""
        + "<span style=\"display: block; white-space: nowrap; font-family: monospace;\">"
        + "%s&nbsp;&nbsp;%s&nbsp;&nbsp;%s"
        + "</span>",
        htmlRenderer.render(a),
        htmlRenderer.render(b),
        htmlRenderer.render(object)));
  }

  @Test
  public void renders_invoked_with_many_arguments() {
    given(htmlRenderer = new HtmlRenderer());
    when(htmlRenderer.render(invoked(instance, method, asList(a, b, c))));
    thenReturned(format("%s.%s(%s,&nbsp;%s,&nbsp;%s)", instance, method.getName(), a, b, c));
  }

  @Test
  public void renders_invoked_with_no_arguments() {
    given(htmlRenderer = new HtmlRenderer());
    when(htmlRenderer.render(invoked(instance, method, asList())));
    thenReturned(format("%s.%s()", instance, method.getName()));
  }

  @Test
  public void renders_invoked_instance() {
    when(htmlRenderer.render(invoked(instance, method, asList(a, b, c))));
    thenReturned(format("%s.%s(%s,&nbsp;%s,&nbsp;%s)", instance, method.getName(), a, b, c));
  }

  @Test
  public void renders_invoked_arguments() {
    when(htmlRenderer.render(invoked(instance, method, asList(a, b, c))));
    thenReturned(format("%s.%s(%s,&nbsp;%s,&nbsp;%s)", instance, method.getName(), a, b, c));
  }

  @Test
  public void renders_returned_object() {
    when(htmlRenderer.render(returned(object)));
    thenReturned(format("returned&nbsp;%s", object));
  }

  @Test
  public void renders_returned_void() {
    given(htmlRenderer = new HtmlRenderer());
    when(htmlRenderer.render(returned()));
    thenReturned("returned");
  }

  @Test
  public void renders_thrown() {
    given(htmlRenderer = new HtmlRenderer());
    when(htmlRenderer.render(thrown(throwable)));
    thenReturned(format("thrown&nbsp;%s", htmlRenderer.render(throwable)));
  }

  @Test
  public void renders_stack_trace_depth() {
    given(htmlRenderer = new HtmlRenderer());
    when(htmlRenderer.render(invocationDepth(3)));
    thenReturned("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
  }

  @Test
  public void renders_empty_list() {
    given(htmlRenderer = new HtmlRenderer());
    when(htmlRenderer.render(asList()));
    thenReturned("List[]");
  }

  @Test
  public void renders_list() {
    given(htmlRenderer = new HtmlRenderer());
    when(htmlRenderer.render(asList(a, b, c)));
    thenReturned(format("List[%s,&nbsp;%s,&nbsp;%s]", a, b, c));
  }

  @Test
  public void renders_array() {
    given(htmlRenderer = new HtmlRenderer());
    when(htmlRenderer.render(new Object[] { a, b, c }));
    thenReturned(format("[%s,&nbsp;%s,&nbsp;%s]", a, b, c));
  }
}
