package com.mikosik.logbuddy.formatter;

import static com.mikosik.logbuddy.formatter.InvokedMethod.invoked;
import static com.mikosik.logbuddy.formatter.Text.text;
import static java.util.stream.Collectors.joining;

import com.mikosik.logbuddy.Formatter;

public class DefaultFormatter implements Formatter {
  public DefaultFormatter() {}

  public String format(Object object) {
    if (object instanceof Text) {
      return formatImpl((Text) object);
    } else if (object instanceof Invocation) {
      return formatImpl((Invocation) object);
    } else if (object instanceof InvokedMethod) {
      return formatImpl((InvokedMethod) object);
    } else if (object instanceof Returned) {
      return formatImpl((Returned) object);
    } else if (object instanceof Thrown) {
      return formatImpl((Thrown) object);
    } else {
      return formatImpl(object);
    }
  }

  private String formatImpl(Text text) {
    return text.string;
  }

  private String formatImpl(Invocation invocation) {
    String argumentsString = invocation.arguments.stream()
        .map(this::format)
        .collect(joining(format(text(", "))));
    return new StringBuilder()
        .append(format(invocation.instance))
        .append(format(text(".")))
        .append(format(invoked(invocation.method)))
        .append(format(text("(")))
        .append(argumentsString)
        .append(format(text(")")))
        .toString();
  }

  private String formatImpl(InvokedMethod invokedMethod) {
    return format(text(invokedMethod.method.getName()));
  }

  private String formatImpl(Returned returned) {
    return format(text("returned ")) + format(returned.object);
  }

  private String formatImpl(Thrown thrown) {
    return format(text("thrown ")) + format(thrown.throwable);
  }

  private String formatImpl(Object object) {
    return format(text(String.valueOf(object)));
  }
}
