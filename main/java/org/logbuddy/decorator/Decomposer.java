package org.logbuddy.decorator;

import java.util.List;

public interface Decomposer {
  List<Object> decompose(Object composite);
}
