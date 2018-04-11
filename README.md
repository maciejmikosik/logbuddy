Logbuddy is logging library that doesn't require you to write explicit log statements.
It traverses graph of your dependencies and wraps them with logging proxies.
This way each invocation on service (instance, method, arguments, returned object or thrown exception) is logged without polluting your production code with log statements.

Quick example.

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

prints

```
main  2018-03-27T11:57:27.493Z    App.start()
main  2018-03-27T11:57:27.502Z      Service#1404928347.serve()
main  2018-03-27T11:57:27.502Z      returned
main  2018-03-27T11:57:27.502Z      Service#949057310.serve()
main  2018-03-27T11:57:27.502Z      returned
main  2018-03-27T11:57:27.503Z    returned
```

Logbuddy offers possibility to generate html logs that contain images, graphs etc.

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

generates

![sinusoid](doc/md/sinusoid.png)

More in [Documentation](doc/md/documentation.md).
