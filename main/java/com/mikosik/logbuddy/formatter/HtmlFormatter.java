package com.mikosik.logbuddy.formatter;

public class HtmlFormatter extends DefaultFormatter {
  public String format(Object object) {
    if (object instanceof Text) {
      return escape(((Text) object).string);
    } else if (object instanceof Html) {
      return ((Html) object).content;
    } else {
      return super.format(object);
    }
  }

  private static String escape(String string) {
    return string
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace(" ", "&nbsp;")
        .replace("\t", "&nbsp;&nbsp;");
  }
}
