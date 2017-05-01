package org.logbuddy.testing;

import java.io.PrintStream;

import org.junit.rules.ExternalResource;

public class RestoreStandardStreams extends ExternalResource {
  private PrintStream stdout, stderr;

  protected void before() {
    stdout = System.out;
    stderr = System.err;
  }

  protected void after() {
    System.setOut(stdout);
    System.setErr(stderr);
  }
}
