package org.logbuddy.testing;

public class Threads {
  public static Thread startInNewThread(Runnable runnable) {
    Thread thread = new Thread(runnable);
    thread.start();
    return thread;
  }
}
