package org.logbuddy.bind;

import static java.lang.String.format;
import static org.logbuddy.Message.message;
import static org.logbuddy.bind.LoggingStream.loggingStream;
import static org.logbuddy.testing.Threads.startInNewThread;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenCalled;
import static org.testory.Testory.thenCalledInOrder;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

import org.junit.Before;
import org.junit.Test;
import org.logbuddy.LogBuddyException;
import org.logbuddy.Logger;

public class TestLoggingStream {
  private Charset charset;
  private String prefix;
  private Logger logger;
  private OutputStream output;
  private String a, b;
  private CountDownLatch latch;
  private Thread threadA, threadB;

  @Before
  public void before() {
    givenTest(this);
    given(charset = Charset.forName("utf8"));
  }

  @Test
  public void logs_separate_prefixed_message_per_line() {
    given(output = loggingStream(prefix, charset, logger));
    when(() -> {
      output.write(bytes(a));
      output.write(bytes("\n"));
      output.write(bytes(b));
      output.write(bytes("\n"));
    });
    thenCalledInOrder(logger).log(message(prefix + a));
    thenCalledInOrder(logger).log(message(prefix + b));
  }

  @Test
  public void messages_are_buffered_per_thread() {
    given(output = loggingStream(prefix, charset, logger));
    given(latch = new CountDownLatch(2));
    given(threadA = startInNewThread(new Runnable() {
      public void run() {
        try {
          output.write(bytes(a));
          latch.countDown();
          latch.await();
          output.write(bytes("\n"));
        } catch (IOException | InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }));
    given(threadB = startInNewThread(new Runnable() {
      public void run() {
        try {
          output.write(bytes(b));
          latch.countDown();
          latch.await();
          output.write(bytes("\n"));
        } catch (IOException | InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }));
    when(() -> {
      threadA.join();
      threadB.join();
    });
    thenReturned();
    thenCalled(logger).log(message(prefix + a));
    thenCalled(logger).log(message(prefix + b));
  }

  @Test
  public void implements_to_string() {
    given(output = loggingStream(prefix, charset, logger));
    when(output.toString());
    thenReturned(format("loggingStream(%s, %s, %s)", prefix, charset, logger));
  }

  @Test
  public void prefix_cannot_be_null() {
    given(prefix = null);
    when(() -> loggingStream(prefix, charset, logger));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void charset_cannot_be_null() {
    given(charset = null);
    when(() -> loggingStream(prefix, charset, logger));
    thenThrown(LogBuddyException.class);
  }

  @Test
  public void logger_cannot_be_null() {
    given(logger = null);
    when(() -> loggingStream(prefix, charset, logger));
    thenThrown(LogBuddyException.class);
  }

  private byte[] bytes(String string) {
    return string.getBytes(charset);
  }
}
