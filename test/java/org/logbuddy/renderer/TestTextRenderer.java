package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.logbuddy.model.Depth.depth;
import static org.logbuddy.model.Invocation.invocation;
import static org.logbuddy.model.Property.property;
import static org.logbuddy.model.Returned.returned;
import static org.logbuddy.model.Thrown.thrown;
import static org.logbuddy.renderer.Text.text;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.lang.reflect.Method;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.Renderer;

public class TestTextRenderer {
  private Object object;
  private Renderer<Text> renderer;
  private Method method;
  private Object instance, a, b, c;
  private String string;
  private Throwable throwable;
  private ZonedDateTime time;
  private Thread thread;
  private Object model;

  @Before
  public void before() {
    given(time = ZonedDateTime.now());
    givenTest(this);
    given(renderer = new TextRenderer());
    given(throwable = new Throwable());
  }

  @Test
  public void renders_object() {
    when(renderer.render(object));
    thenReturned(text(object.toString()));
  }

  @Test
  public void renders_null() {
    when(renderer.render(null));
    thenReturned(text("null"));
  }

  @Test
  public void renders_invocation_with_many_arguments() {
    when(renderer.render(invocation(instance, method, asList(a, b, c))));
    thenReturned(text(format("%s.%s(%s, %s, %s)", instance, method.getName(), a, b, c)));
  }

  @Test
  public void renders_invocation_with_no_arguments() {
    when(renderer.render(invocation(instance, method, asList())));
    thenReturned(text(format("%s.%s()", instance, method.getName())));
  }

  @Test
  public void renders_invocation_instance() {
    given(renderer = new TextRenderer() {
      public Text render(Object model) {
        if (model == instance) {
          return text(string);
        } else {
          return super.render(model);
        }
      }
    });
    when(renderer.render(invocation(instance, method, asList(a, b, c))));
    thenReturned(text(format("%s.%s(%s, %s, %s)", string, method.getName(), a, b, c)));
  }

  @Test
  public void renders_invocation_arguments() {
    given(renderer = new TextRenderer() {
      public Text render(Object model) {
        if (model == b) {
          return text(string);
        } else {
          return super.render(model);
        }
      }
    });
    when(renderer.render(invocation(instance, method, asList(a, b, c))));
    thenReturned(text(format("%s.%s(%s, %s, %s)", instance, method.getName(), a, string, c)));
  }

  @Test
  public void renders_returned() {
    given(renderer = new TextRenderer() {
      public Text render(Object model) {
        if (model == object) {
          return text(string);
        } else {
          return super.render(model);
        }
      }
    });
    when(renderer.render(returned(object)));
    thenReturned(text(format("returned %s", string)));
  }

  @Test
  public void renders_thrown() {
    given(renderer = new TextRenderer());
    when(renderer.render(thrown(throwable)));
    thenReturned(text(format("thrown %s", throwable.toString())));
  }

  @Test
  public void renders_stack_trace_depth() {
    when(renderer.render(depth(3, model)));
    thenReturned(text(format("\t\t\t%s", model)));
  }

  @Test
  public void renders_empty_list() {
    when(renderer.render(asList()));
    thenReturned(text(format("List[]")));
  }

  @Test
  public void renders_list() {
    when(renderer.render(asList(a, b, c)));
    thenReturned(text(format("List[%s, %s, %s]", a, b, c)));
  }

  @Test
  public void renders_property() {
    given(renderer = new TextRenderer() {
      public Text render(Object model) {
        if (model == object) {
          return text(string);
        } else {
          return super.render(model);
        }
      }
    });
    when(renderer.render(property(object, model)));
    thenReturned(text(format("%s\t%s", string, model)));
  }

  @Test
  public void renders_time_in_utc() {
    given(time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneOffset.UTC));
    when(renderer.render(time));
    thenReturned(text("1970-01-01T00:00:00.000Z"));
  }

  @Test
  public void renders_time_using_timezone() {
    given(time = ZonedDateTime.ofInstant(Instant.ofEpochMilli(0), ZoneOffset.ofHours(2)));
    when(renderer.render(time));
    thenReturned(text("1970-01-01T02:00:00.000+02:00"));
  }

  @Test
  public void renders_thread() {
    given(thread = new Thread(string));
    when(renderer.render(thread));
    thenReturned(text(format("Thread(%s)", string)));
  }
}
