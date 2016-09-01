package com.mikosik.logbuddy.formatter;

import static com.mikosik.logbuddy.formatter.Html.html;
import static java.lang.String.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;

import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;

import com.mikosik.logbuddy.Formatter;

@RunWith(QuackeryRunner.class)
public class TestHtmlFormatter {
  @Quackery
  public static Suite test_format() {
    return suite(TestHtmlFormatter.class.getName())
        .add(testFormat("string", "string"))
        .add(testFormat("&", "&amp;"))
        .add(testFormat("<", "&lt;"))
        .add(testFormat(">", "&gt;"))
        .add(testFormat(" ", "&nbsp;"))
        .add(testFormat("\t", "&nbsp;&nbsp;"))
        .add(testFormat(html("<b>&</b>"), "<b>&</b>"));
  }

  private static Case testFormat(Object object, String formatted) {
    return newCase(format("formats %s to %s", object, formatted), () -> {
      Formatter formatter = new HtmlFormatter();
      String actual = formatter.format(object);
      assertEquals(actual, formatted);
    });
  }
}
