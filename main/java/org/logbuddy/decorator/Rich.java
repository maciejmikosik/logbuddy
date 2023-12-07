package org.logbuddy.decorator;

import static org.logbuddy.decorator.ComponentsDecorator.components;
import static org.logbuddy.decorator.ConnectingLoggerDecorator.connecting;
import static org.logbuddy.decorator.DefaultDecomposer.decomposer;
import static org.logbuddy.decorator.InvocationDecorator.invocationDecorator;
import static org.logbuddy.decorator.RecursiveDecomposer.recursive;
import static org.logbuddy.decorator.TryingDecorator.trying;
import static org.logbuddy.logger.Fuse.fuse;
import static org.logbuddy.logger.InvocationDepthLogger.invocationDepth;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;

public class Rich {
  public static Logger rich(Logger logger) {
    return fuse().install(invocationDepth(logger));
  }

  public static Decorator richDecorator(Logger logger) {
    return connecting(logger, trying(invocationDecorator(logger)));
  }

  public static Decorator traversing(Decorator decorator) {
    return new Decorator() {
      public <T> T decorate(T decorable) {
        recursive(decomposer())
            .decompose(decorable)
            .forEach(components(decorator)::decorate);
        return decorator.decorate(decorable);
      }
    };
  }
}
