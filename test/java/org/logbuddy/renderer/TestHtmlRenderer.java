package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.logbuddy.model.Depth.depth;
import static org.logbuddy.model.Invocation.invocation;
import static org.logbuddy.model.Property.property;
import static org.logbuddy.model.Returned.returned;
import static org.logbuddy.model.Thrown.thrown;
import static org.logbuddy.renderer.Html.html;
import static org.logbuddy.renderer.Text.text;
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
  private Renderer<Html> htmlRenderer;
  private Renderer<Text> textRenderer;
  private Object object;
  private String string;
  private Method method;
  private Object instance, a, b, c;
  private Throwable throwable;
  private Object model;

  @Before
  public void before() {
    givenTest(this);
    given(throwable = new Throwable());
  }

  @Test
  public void delegates_rendering_object() {
    given(htmlRenderer = new HtmlRenderer(textRenderer));
    given(willReturn(text(string)), textRenderer).render(object);
    when(htmlRenderer.render(object));
    thenReturned(html(string));
  }

  @Test
  public void delegates_rendering_null() {
    given(htmlRenderer = new HtmlRenderer(textRenderer));
    given(willReturn(text(string)), textRenderer).render(null);
    when(htmlRenderer.render(null));
    thenReturned(html(string));
  }

  @Test
  public void escapes_html_characters() {
    given(htmlRenderer = new HtmlRenderer(textRenderer));
    given(willReturn(text("&_<_>_ _\t")), textRenderer).render(object);
    when(htmlRenderer.render(object));
    thenReturned(html("&amp;_&lt;_&gt;_&nbsp;_&nbsp;&nbsp;"));
  }

  @Test
  public void renders_invocation_with_many_arguments() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())));
    when(htmlRenderer.render(invocation(instance, method, asList(a, b, c))));
    thenReturned(html(format("%s.%s(%s, %s, %s)", instance, method.getName(), a, b, c)));
  }

  @Test
  public void renders_invocation_with_no_arguments() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())));
    when(htmlRenderer.render(invocation(instance, method, asList())));
    thenReturned(html(format("%s.%s()", instance, method.getName())));
  }

  @Test
  public void renders_invocation_instance() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())) {
      public Html render(Object model) {
        if (model == instance) {
          return html(string);
        } else {
          return super.render(model);
        }
      }
    });
    when(htmlRenderer.render(invocation(instance, method, asList(a, b, c))));
    thenReturned(html(format("%s.%s(%s, %s, %s)", string, method.getName(), a, b, c)));
  }

  @Test
  public void renders_invocation_arguments() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())) {
      public Html render(Object model) {
        if (model == b) {
          return html(string);
        } else {
          return super.render(model);
        }
      }
    });
    when(htmlRenderer.render(invocation(instance, method, asList(a, b, c))));
    thenReturned(html(format("%s.%s(%s, %s, %s)", instance, method.getName(), a, string, c)));
  }

  @Test
  public void renders_returned() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())) {
      public Html render(Object model) {
        if (model == object) {
          return html(string);
        } else {
          return super.render(model);
        }
      }
    });
    when(htmlRenderer.render(returned(object)));
    thenReturned(html(format("returned %s", string)));
  }

  @Test
  public void renders_thrown() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())));
    when(htmlRenderer.render(thrown(throwable)));
    thenReturned(html(format("thrown %s", htmlRenderer.render(throwable).body)));
  }

  @Test
  public void renders_stack_trace_depth() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())));
    when(htmlRenderer.render(depth(3, model)));
    thenReturned(html(format("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;%s", model)));
  }

  @Test
  public void renders_empty_list() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())));
    when(htmlRenderer.render(asList()));
    thenReturned(html("List[]"));
  }

  @Test
  public void renders_list() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())));
    when(htmlRenderer.render(asList(a, b, c)));
    thenReturned(html(format("List[%s,&nbsp;%s,&nbsp;%s]", a, b, c)));
  }

  @Test
  public void renders_array() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())));
    when(htmlRenderer.render(new Object[] { a, b, c }));
    thenReturned(html(format("[%s,&nbsp;%s,&nbsp;%s]", a, b, c)));
  }

  @Test
  public void renders_property() {
    given(htmlRenderer = new HtmlRenderer(model -> text(model.toString())) {
      public Html render(Object model) {
        if (model == object) {
          return html(string);
        } else {
          return super.render(model);
        }
      }
    });
    when(htmlRenderer.render(property(object, model)));
    thenReturned(html(format("%s&nbsp;&nbsp;%s", string, model)));
  }

  @Test
  public void checks_nulls() {
    when(() -> new HtmlRenderer(null));
    thenThrown(LogBuddyException.class);
  }
}
