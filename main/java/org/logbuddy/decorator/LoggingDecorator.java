package org.logbuddy.decorator;

import static java.util.Arrays.asList;
import static org.logbuddy.LogBuddyException.check;
import static org.logbuddy.model.Invocation.invocation;
import static org.logbuddy.model.Returned.returned;
import static org.logbuddy.model.Thrown.thrown;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.matcher.ElementMatchers;

public class LoggingDecorator implements Decorator {
  private final Objenesis objenesis = new ObjenesisStd();
  private final ByteBuddy byteBuddy = new ByteBuddy();

  private final Logger logger;

  private LoggingDecorator(Logger logger) {
    this.logger = logger;
  }

  public static Decorator logging(Logger logger) {
    check(logger != null);
    return new LoggingDecorator(preventChainReaction(logger));
  }

  public <T> T decorate(T decorable) {
    check(decorable != null);
    Class<?> decorableType = byteBuddy
        .subclass(decorable.getClass())
        .method(ElementMatchers.any())
        .intercept(MethodDelegation.to(new DecorateHandler(decorable)))
        .make()
        .load(Thread.currentThread().getContextClassLoader(), ClassLoadingStrategy.Default.INJECTION)
        .getLoaded();
    return (T) objenesis.newInstance(decorableType);
  }

  public class DecorateHandler {
    private final Object original;

    public DecorateHandler(Object original) {
      this.original = original;
    }

    @RuntimeType
    public Object handle(@Origin Method method, @AllArguments Object[] arguments) throws Throwable {
      logger.log(invocation(original, method, asList(arguments)));
      try {
        Object result = method.invoke(original, arguments);
        logger.log(returned(result));
        return result;
      } catch (InvocationTargetException e) {
        Throwable cause = e.getCause();
        logger.log(thrown(cause));
        throw cause;
      }
    }
  }

  private static Logger preventChainReaction(Logger logger) {
    return new Logger() {
      private final ThreadLocal<Boolean> isEnabled = new ThreadLocal<Boolean>() {
        protected Boolean initialValue() {
          return true;
        }
      };

      public void log(Object model) {
        if (isEnabled.get()) {
          isEnabled.set(false);
          logger.log(model);
          isEnabled.set(true);
        }
      }
    };
  }
}
