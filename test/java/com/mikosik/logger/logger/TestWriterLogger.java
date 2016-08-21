package com.mikosik.logger.logger;

import static com.mikosik.logger.logger.WriterLogger.logger;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenEqual;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Before;
import org.junit.Test;

import com.mikosik.logger.Logger;

public class TestWriterLogger {
  private Writer writer;
  private Logger logger;
  private String message;

  @Before
  public void before() {
    givenTest(this);
    given(writer = new StringWriter());
  }

  @Test
  public void writes_text_to_writer() throws IOException {
    given(logger = logger(writer));
    when(() -> logger.log(message));
    thenReturned();
    thenEqual(writer.toString(), message + "\n");
  }

  @Test
  public void checks_null() {
    when(() -> logger(null));
    thenThrown(NullPointerException.class);
  }
}
