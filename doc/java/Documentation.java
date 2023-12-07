import static java.awt.Color.BLUE;
import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import static org.junit.Assert.assertSame;
import static org.logbuddy.Message.message;
import static org.logbuddy.bind.Slf4jBinder.slf4jBinder;
import static org.logbuddy.bind.StdioBinder.stdioBinder;
import static org.logbuddy.decorator.CachingDecorator.caching;
import static org.logbuddy.decorator.ComponentsDecorator.components;
import static org.logbuddy.decorator.ComposedDecorator.compose;
import static org.logbuddy.decorator.InjectingDecorator.injecting;
import static org.logbuddy.decorator.InvocationDecorator.invocationDecorator;
import static org.logbuddy.decorator.JdkDecorator.jdk;
import static org.logbuddy.decorator.TraversingDecorator.traversing;
import static org.logbuddy.logger.AsynchronousLogger.asynchronous;
import static org.logbuddy.logger.CatchingLogger.catching;
import static org.logbuddy.logger.ComposedLogger.compose;
import static org.logbuddy.logger.Fuse.fuse;
import static org.logbuddy.logger.InvocationDepthLogger.invocationDepth;
import static org.logbuddy.logger.NoLogger.noLogger;
import static org.logbuddy.logger.SynchronizedLogger.synchronize;
import static org.logbuddy.logger.ThreadLogger.thread;
import static org.logbuddy.logger.TimeLogger.time;
import static org.logbuddy.logger.WritingLogger.logger;
import static org.logbuddy.logger.wire.BrowserLogger.browserLogger;
import static org.logbuddy.logger.wire.ConsoleLogger.consoleLogger;
import static org.logbuddy.logger.wire.FileLogger.fileLogger;
import static org.logbuddy.renderer.chart.ChartModel.chartModel;
import static org.logbuddy.renderer.chart.LineChartRenderer.lineChartRenderer;

import java.awt.Color;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.logbuddy.Decorator;
import org.logbuddy.Logger;
import org.logbuddy.logger.Fuse;
import org.logbuddy.renderer.HtmlRenderer;
import org.logbuddy.renderer.TextRenderer;
import org.logbuddy.renderer.chart.ChartModel;
import org.slf4j.event.Level;

public class Documentation {
  public static void main(String[] args) {
    readme_text();
  }

  public static void readme_text() {
    class Service {
      void serve() {}

      public String toString() {
        return "Service#" + hashCode();
      }
    }
    class App {
      Service serviceA = new Service();
      Service serviceB = new Service();

      void start() {
        serviceA.serve();
        serviceB.serve();
      }

      public String toString() {
        return "App";
      }
    }

    Logger logger = thread(time(Clock.systemUTC(), invocationDepth(consoleLogger(new TextRenderer()))));
    Decorator decorator = traversing(invocationDecorator(logger));
    decorator.decorate(new App()).start();
  }

  public static void readme_html() {
    HtmlRenderer renderer = new HtmlRenderer() {
      public String render(Object model) {
        return model instanceof ChartModel
            ? lineChartRenderer()
                .width(500)
                .height(100)
                .color(BLUE)
                .render((ChartModel) model)
            : super.render(model);
      }
    };
    Logger logger = fileLogger(Paths.get("log.html"), renderer);
    List<Double> sinusoid = range(0, 100)
        .mapToDouble(i -> i * 0.1)
        .map(Math::sin)
        .boxed()
        .collect(toList());
    logger.log(message(chartModel(sinusoid)));
  }

  public static void renderer_message() {
    Logger logger = consoleLogger(new TextRenderer());
    logger.log(message("content"));
  }

  public static void renderer_attribute() {
    Logger logger = consoleLogger(new TextRenderer());
    logger.log(message("content").attribute(Instant.now()));
  }

  public static void renderer_array() {
    Logger logger = consoleLogger(new TextRenderer());
    logger.log(message(new int[] { 1, 2, 3 }));
  }

  public static void renderer_html() {
    Logger logger = consoleLogger(new HtmlRenderer());
    logger.log(message("content"));
  }

  @SuppressWarnings("unused")
  public static void logger_destination_console() {
    Logger logger = consoleLogger(new TextRenderer());
  }

  @SuppressWarnings("unused")
  public static void logger_destination_file() {
    Logger textLogger = fileLogger(Paths.get("log.txt"), new TextRenderer());
    Logger htmlLogger = fileLogger(Paths.get("log.html"), new HtmlRenderer());
  }

  @SuppressWarnings("unused")
  public static void logger_destination_browser() {
    Logger logger = browserLogger(new HtmlRenderer());
  }

  @SuppressWarnings("unused")
  public static void logger_destination_writing() {
    Charset charset = Charset.forName("utf8");
    Writer writer = new OutputStreamWriter(System.err);
    Logger logger = logger(writer, new TextRenderer());
  }

  @SuppressWarnings("unused")
  public static void logger_destination_no() {
    Logger logger = noLogger();
  }

  @SuppressWarnings("unused")
  public static void logger_destination_compose() {
    Logger logger = compose(
        consoleLogger(new TextRenderer()),
        fileLogger(Paths.get("log.html"), new HtmlRenderer()));
  }

  public static void logger_attribute_time() {
    Logger logger = time(Clock.systemUTC(), consoleLogger(new TextRenderer()));
    logger.log(message("content"));
  }

  public static void logger_attribute_thread() {
    Logger logger = thread(consoleLogger(new TextRenderer()));
    logger.log(message("content"));
  }

  @SuppressWarnings("unused")
  public static void logger_safety_synchronized() {
    Logger logger = synchronize(consoleLogger(new TextRenderer()));
  }

  @SuppressWarnings("unused")
  public static void logger_safety_asynchronous() {
    Logger logger = asynchronous(consoleLogger(new TextRenderer()));
  }

  @SuppressWarnings("unused")
  public static void logger_safety_fuse() {
    Logger logger = fuse().install(consoleLogger(new TextRenderer()));
  }

  @SuppressWarnings("unused")
  public static void logger_safety_fuse_threads() {
    Fuse fuse = fuse();
    Logger logger = fuse.install(asynchronous(fuse.install(consoleLogger(new TextRenderer()))));
  }

  public static void logger_safety_catching() {
    Logger logger = catching(consoleLogger(new TextRenderer()));
    logger.log(message(new Object() {
      public String toString() {
        throw new RuntimeException("failed");
      }
    }));
  }

  public static void decorator_invocation_returned() {
    Decorator decorator = invocationDecorator(consoleLogger(new TextRenderer()));
    List<String> list = decorator.decorate(new ArrayList<>(asList("a", "b", "c")));
    list.contains("x");
  }

  public static void decorator_invocation_thrown() {
    Decorator decorator = invocationDecorator(consoleLogger(new TextRenderer()));
    List<String> list = decorator.decorate(new ArrayList<>(asList("a", "b", "c")));
    list.remove(5);
  }

  public static void decorator_invocation_depth() {
    Logger logger = fuse().install(invocationDepth(consoleLogger(new TextRenderer())));
    Decorator decorator = invocationDecorator(logger);
    Color red = decorator.decorate(Color.RED);
    Color green = decorator.decorate(Color.GREEN);
    List<Object> list = decorator.decorate(new ArrayList<>(asList(red, green)));
    list.contains(green);
  }

  public static void decorator_jdk_fail() {
    Logger logger = consoleLogger(new TextRenderer());
    Decorator decorator = invocationDecorator(logger);
    List<String> decorable = Arrays.asList("string");
    decorator.decorate(decorable);
  }

  public static void decorator_jdk() {
    Logger logger = consoleLogger(new TextRenderer());
    Decorator decorator = jdk(invocationDecorator(logger));
    List<String> decorable = Arrays.asList("string");
    List<String> decorated = decorator.decorate(decorable);
    decorated.get(0);
  }

  public static void decorator_components() {
    class Service {
      private final Color red = Color.RED;
      private final Color green = Color.GREEN;
      private final Color blue = Color.BLUE;

      public String toString() {
        return "" + red + green + blue;
      }
    }
    Logger logger = consoleLogger(new TextRenderer());
    Decorator decorator = components(invocationDecorator(logger));
    Service service = new Service();
    decorator.decorate(service);
    service.toString();
  }

  public static void decorator_traversing() {
    class Service {
      void serve() {}

      public String toString() {
        return "Service#" + hashCode();
      }
    }
    class App {
      Service serviceA = new Service();
      Service serviceB = new Service();

      void start() {
        serviceA.serve();
        serviceB.serve();
      }

      public String toString() {
        return "App";
      }
    }

    Logger logger = invocationDepth(consoleLogger(new TextRenderer()));
    Decorator decorator = traversing(invocationDecorator(logger));

    decorator.decorate(new App()).start();
  }

  public static void decorator_traversing_filter(Decorator decorator) {
    traversing(decorator)
        .filter(field -> !field.getType().isArray());
  }

  public static void decorator_caching(Decorator decorator, Object object) {
    Decorator cachingDecorator = caching(decorator);
    assertSame(
        cachingDecorator.decorate(object),
        cachingDecorator.decorate(object));

  }

  public static void decorator_injecting() {
    class Service {
      Logger logger = noLogger();

      public void serve() {
        logger.log(message("adhoc message"));
      }
    }

    Logger logger = consoleLogger(new TextRenderer());
    Decorator decorator = injecting(logger);

    decorator.decorate(new Service()).serve();
  }

  public static void decorator_compose() {
    class Service {
      Logger logger = noLogger();

      public void serve() {
        logger.log(message("adhoc message"));
      }

      public String toString() {
        return "Service#" + hashCode();
      }
    }

    Logger logger = invocationDepth(consoleLogger(new TextRenderer()));
    Decorator decorator = compose(
        invocationDecorator(logger),
        injecting(logger));

    decorator.decorate(new Service()).serve();
  }

  public static void custom_render_color_original() {
    TextRenderer renderer = new TextRenderer();
    Logger logger = consoleLogger(renderer);
    logger.log(message(Color.RED));
  }

  public static void custom_render_color() {
    TextRenderer renderer = new TextRenderer() {
      public String render(Object model) {
        return model instanceof Color
            ? format("Color(#%06X)", ((Color) model).getRGB() & 0x00FFFFFF)
            : super.render(model);
      }
    };
    Logger logger = consoleLogger(renderer);
    logger.log(message(Color.GREEN));
  }

  public static void integrate_stdio() {
    Logger logger = consoleLogger(new TextRenderer());
    stdioBinder(Charset.forName("utf8"), logger).bind();

    logger.log(message("message"));
    System.out.println("message");
    System.err.println("message");
  }

  public static void integrate_slf4j() {
    Logger logger = consoleLogger(new TextRenderer());
    slf4jBinder(Level.INFO, logger).bind();
  }
}
