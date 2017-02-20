package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.logbuddy.Message.message;
import static org.logbuddy.model.Depth.depth;
import static org.logbuddy.model.Invocation.invocation;
import static org.logbuddy.model.Returned.returned;
import static org.logbuddy.model.Thrown.thrown;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Renderer;

public class TestHtmlRenderer {
  private Renderer<String> htmlRenderer, textRenderer;
  private Object object;
  private String string;
  private Method method;
  private Object instance, a, b, c;
  private Throwable throwable;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new Throwable());
  }

  @Test
  public void delegates_rendering_object() {
    given(htmlRenderer = new HtmlRenderer(textRenderer));
    given(willReturn(string), textRenderer).render(object);
    when(htmlRenderer.render(object));
    thenReturned(string);
  }

  @Test
  public void delegates_rendering_null() {
    given(htmlRenderer = new HtmlRenderer(textRenderer));
    given(willReturn(string), textRenderer).render(null);
    when(htmlRenderer.render(null));
    thenReturned(string);
  }

  @Test
  public void escapes_html_characters() {
    given(htmlRenderer = new HtmlRenderer(textRenderer));
    given(willReturn("&_<_>_ _\t"), textRenderer).render(object);
    when(htmlRenderer.render(object));
    thenReturned("&amp;_&lt;_&gt;_&nbsp;_&nbsp;&nbsp;");
  }

  @Test
  public void renders_message() {
    given(textRenderer = model -> format("rendered(%s)", model));
    given(htmlRenderer = new HtmlRenderer(textRenderer));
    when(htmlRenderer.render(message(object)
        .attribute(a)
        .attribute(b)));
    thenReturned(format(""
        + "<span style=\"display: block; white-space: nowrap; font-family: monospace;\">"
        + "%s&nbsp;&nbsp;%s&nbsp;&nbsp;%s"
        + "</span>"
        + "\n",
        htmlRenderer.render(a),
        htmlRenderer.render(b),
        htmlRenderer.render(object)));
  }

  @Test
  public void renders_invocation_with_many_arguments() {
    given(htmlRenderer = new HtmlRenderer(Object::toString));
    when(htmlRenderer.render(invocation(instance, method, asList(a, b, c))));
    thenReturned(format("%s.%s(%s, %s, %s)", instance, method.getName(), a, b, c));
  }

  @Test
  public void renders_invocation_with_no_arguments() {
    given(htmlRenderer = new HtmlRenderer(Object::toString));
    when(htmlRenderer.render(invocation(instance, method, asList())));
    thenReturned(format("%s.%s()", instance, method.getName()));
  }

  @Test
  public void renders_invocation_instance() {
    given(htmlRenderer = new HtmlRenderer(Object::toString) {
      public String render(Object model) {
        if (model == instance) {
          return string;
        } else {
          return super.render(model);
        }
      }
    });
    when(htmlRenderer.render(invocation(instance, method, asList(a, b, c))));
    thenReturned(format("%s.%s(%s, %s, %s)", string, method.getName(), a, b, c));
  }

  @Test
  public void renders_invocation_arguments() {
    given(htmlRenderer = new HtmlRenderer(Object::toString) {
      public String render(Object model) {
        if (model == b) {
          return string;
        } else {
          return super.render(model);
        }
      }
    });
    when(htmlRenderer.render(invocation(instance, method, asList(a, b, c))));
    thenReturned(format("%s.%s(%s, %s, %s)", instance, method.getName(), a, string, c));
  }

  @Test
  public void renders_returned() {
    given(htmlRenderer = new HtmlRenderer(Object::toString) {
      public String render(Object model) {
        if (model == object) {
          return string;
        } else {
          return super.render(model);
        }
      }
    });
    when(htmlRenderer.render(returned(object)));
    thenReturned(format("returned %s", string));
  }

  @Test
  public void renders_thrown() {
    given(htmlRenderer = new HtmlRenderer(Object::toString));
    when(htmlRenderer.render(thrown(throwable)));
    thenReturned(format("thrown %s", htmlRenderer.render(throwable)));
  }

  @Test
  public void renders_stack_trace_depth() {
    given(htmlRenderer = new HtmlRenderer(Object::toString));
    when(htmlRenderer.render(depth(3)));
    thenReturned("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
  }

  @Test
  public void renders_empty_list() {
    given(htmlRenderer = new HtmlRenderer(Object::toString));
    when(htmlRenderer.render(asList()));
    thenReturned("List[]");
  }

  @Test
  public void renders_list() {
    given(htmlRenderer = new HtmlRenderer(Object::toString));
    when(htmlRenderer.render(asList(a, b, c)));
    thenReturned(format("List[%s,&nbsp;%s,&nbsp;%s]", a, b, c));
  }

  @Test
  public void renders_array() {
    given(htmlRenderer = new HtmlRenderer(Object::toString));
    when(htmlRenderer.render(new Object[] { a, b, c }));
    thenReturned(format("[%s,&nbsp;%s,&nbsp;%s]", a, b, c));
  }

  @Test
  public void checks_nulls() {
    when(() -> new HtmlRenderer(null));
    thenThrown(LogBuddyException.class);
  }
}
