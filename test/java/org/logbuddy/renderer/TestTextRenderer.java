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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Renderer;

public class TestTextRenderer {
  private Object object;
  private Renderer<String> renderer;
  private Method method;
  private Object instance, a, b, c;
  private String string;
  private Throwable throwable;
  private ZonedDateTime time;
  private Thread thread;
  private StringWriter buffer;
  private Object attributeA, attributeB;

  @Before
  public void before() {
    given(time = ZonedDateTime.now());
    givenTest(this);
    given(renderer = new TextRenderer());
    given(throwable = new Throwable());
    given(buffer = new StringWriter());
  }

  @Test
  public void renders_object() {
    when(renderer.render(object));
    thenReturned(object.toString());
  }

  @Test
  public void renders_null() {
    when(renderer.render(null));
    thenReturned("null");
  }

  @Test
  public void renders_message() {
    given(object = new Thread("content"));
    given(attributeA = new Thread("attributeA"));
    given(attributeB = new Thread("attributeB"));
    when(renderer.render(message(object)
        .attribute(attributeA)
        .attribute(attributeB)));
    thenReturned(format("%s  %s  %s",
        renderer.render(attributeA),
        renderer.render(attributeB),
        renderer.render(object)));
  }

  @Test
  public void renders_invoked_with_many_arguments() {
    when(renderer.render(invoked(instance, method, asList(a, b, c))));
    thenReturned(format("%s.%s(%s, %s, %s)", instance, method.getName(), a, b, c));
  }

  @Test
  public void renders_invoked_with_no_arguments() {
    when(renderer.render(invoked(instance, method, asList())));
    thenReturned(format("%s.%s()", instance, method.getName()));
  }

  @Test
  public void renders_invoked_instance() {
    given(renderer = new TextRenderer() {
      public String render(Object model) {
        if (model == instance) {
          return string;
        } else {
          return super.render(model);
        }
      }
    });
    when(renderer.render(invoked(instance, method, asList(a, b, c))));
    thenReturned(format("%s.%s(%s, %s, %s)", string, method.getName(), a, b, c));
  }

  @Test
  public void renders_invoked_arguments() {
    given(renderer = new TextRenderer() {
      public String render(Object model) {
        if (model == b) {
          return string;
        } else {
          return super.render(model);
        }
      }
    });
    when(renderer.render(invoked(instance, method, asList(a, b, c))));
    thenReturned(format("%s.%s(%s, %s, %s)", instance, method.getName(), a, string, c));
  }

  @Test
  public void renders_returned_object() {
    given(renderer = new TextRenderer() {
      public String render(Object model) {
        if (model == object) {
          return string;
        } else {
          return super.render(model);
        }
      }
    });
    when(renderer.render(returned(object)));
    thenReturned(format("returned %s", string));
  }

  @Test
  public void renders_returned_void() {
    given(renderer = new TextRenderer());
    when(renderer.render(returned()));
    thenReturned("returned");
  }

  @Test
  public void renders_thrown() {
    given(renderer = new TextRenderer());
    when(renderer.render(thrown(throwable)));
    throwable.printStackTrace(new PrintWriter(buffer));
    thenReturned(format("thrown %s", buffer.toString()));
  }

  @Test
  public void renders_stack_trace_depth() {
    when(renderer.render(invocationDepth(3)));
    thenReturned("      ");
  }

  @Test
  public void renders_empty_list() {
    when(renderer.render(asList()));
    thenReturned(format("List[]"));
  }

  @Test
  public void renders_list() {
    when(renderer.render(asList(a, b, c)));
    thenReturned(format("List[%s, %s, %s]", a, b, c));
  }

  @Test
  public void renders_empty_array() {
    when(renderer.render(new Object[] {}));
    thenReturned("[]");
  }

  @Test
  public void renders_array() {
    when(renderer.render(new Object[] { a, b, c }));
    thenReturned(format("[%s, %s, %s]", a, b, c));
  }

  @Test
  public void renders_primitive_array() {
    when(renderer.render(new int[] { 1, 2, 3 }));
    thenReturned(format("[%s, %s, %s]", 1, 2, 3));
  }

  @Test
  public void renders_time_in_utc() {
    given(time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneOffset.UTC));
    when(renderer.render(time));
    thenReturned("1970-01-01T00:00:00.000Z");
  }

  @Test
  public void renders_time_using_timezone() {
    given(time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneOffset.ofHours(2)));
    when(renderer.render(time));
    thenReturned("1970-01-01T02:00:00.000+02:00");
  }

  @Test
  public void renders_thread() {
    given(thread = new Thread(string));
    when(renderer.render(thread));
    thenReturned(format("Thread(%s)", string));
  }

  @Test
  public void renders_class() {
    given(object = Object.class);
    when(renderer.render(object));
    thenReturned("java.lang.Object");
  }
}
