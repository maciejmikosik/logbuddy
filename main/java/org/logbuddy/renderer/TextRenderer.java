package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.temporal.ChronoField.HOUR_OF_DAY;
import static java.time.temporal.ChronoField.MINUTE_OF_HOUR;
import static java.time.temporal.ChronoField.NANO_OF_SECOND;
import static java.time.temporal.ChronoField.SECOND_OF_MINUTE;
import static java.util.stream.Collectors.joining;
import static org.logbuddy.common.Throwables.stackTrace;
import static org.logbuddy.renderer.Text.text;

import java.lang.reflect.Array;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

import org.logbuddy.Renderer;
import org.logbuddy.model.Depth;
import org.logbuddy.model.Invocation;
import org.logbuddy.model.Property;
import org.logbuddy.model.Returned;
import org.logbuddy.model.Thrown;

public class TextRenderer implements Renderer<Text> {
  private final DateTimeFormatter dateTimeFormatter;

  public TextRenderer() {
    dateTimeFormatter = new DateTimeFormatterBuilder()
        .parseCaseInsensitive()
        .append(ISO_LOCAL_DATE)
        .appendLiteral('T')
        .appendValue(HOUR_OF_DAY, 2)
        .appendLiteral(':')
        .appendValue(MINUTE_OF_HOUR, 2)
        .appendLiteral(':')
        .appendValue(SECOND_OF_MINUTE, 2)
        .appendFraction(NANO_OF_SECOND, 3, 3, true)
        .appendOffsetId()
        .toFormatter();
  }

  public Text render(Object model) {
    if (model == null) {
      return text("null");
    } else if (model instanceof Invocation) {
      return renderImpl((Invocation) model);
    } else if (model instanceof Returned) {
      return renderImpl((Returned) model);
    } else if (model instanceof Thrown) {
      return renderImpl((Thrown) model);
    } else if (model instanceof Depth) {
      return renderImpl((Depth) model);
    } else if (model instanceof Property) {
      return renderImpl((Property) model);
    } else if (model instanceof ZonedDateTime) {
      return renderImpl((ZonedDateTime) model);
    } else if (model instanceof Thread) {
      return renderImpl((Thread) model);
    } else if (model instanceof List) {
      return renderImpl("List", (List<?>) model);
    } else if (model.getClass().isArray()) {
      return renderImpl("", arrayToList(model));
    } else {
      return text(String.valueOf(model));
    }
  }

  private Text renderImpl(Invocation invocation) {
    String renderedArguments = invocation.arguments.stream()
        .map(argument -> render(argument).string)
        .collect(joining(", "));
    return text(format("%s.%s(%s)",
        render(invocation.instance).string,
        invocation.method.getName(),
        renderedArguments));
  }

  private Text renderImpl(Returned returned) {
    return text(format("returned %s", render(returned.object).string));
  }

  private Text renderImpl(Thrown thrown) {
    return text(format("thrown %s", stackTrace(thrown.throwable)));
  }

  private Text renderImpl(Depth depth) {
    String indentation = new String(new char[depth.value]).replace('\0', '\t');
    return text(indentation + render(depth.model).string);
  }

  private Text renderImpl(String prefix, List<?> list) {
    return text(list.stream()
        .map(element -> render(element).string)
        .collect(joining(", ", prefix + "[", "]")));
  }

  private Text renderImpl(Property property) {
    return text(format("%s\t%s",
        render(property.value).string,
        render(property.model).string));
  }

  private Text renderImpl(ZonedDateTime zonedDateTime) {
    return text(dateTimeFormatter.format(zonedDateTime));
  }

  private Text renderImpl(Thread thread) {
    return text(format("Thread(%s)", thread.getName()));
  }

  private static List<Object> arrayToList(Object array) {
    int length = Array.getLength(array);
    List<Object> list = new ArrayList<>(length);
    for (int i = 0; i < length; i++) {
      list.add(Array.get(array, i));
    }
    return list;
  }
}
