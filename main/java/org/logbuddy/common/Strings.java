package org.logbuddy.common;

import static java.util.Arrays.asList;

import java.util.List;

public class Strings {
  public static String times(int n, String string) {
    return new String(new char[n]).replace("\0", string);
  }

  public static List<String> lines(String string) {
    return asList(string.split("\n"));
  }
}
