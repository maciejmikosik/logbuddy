[overview](#overview) | [decorator](#decorator) | [customization](#customization) | [integration](#integration)

# Overview

[renderer](#renderer) | [logger](#logger)

Most popular logging frameworks require you to put log statements inside your production code.
This has negative effects on development.
 - Logs are mixed with application logic making it less readable.
 - Logs need to be updated to include new content when application code is changed.
 - Logs cannot be turned on/off in a fine-grained way without changing production code.

Logbuddy offers alternative approach by allowing you to configure the scope of logs outside of production code.
During wiring stage of your application, you configure what needs to be logged.
Objects in your dependency graph are wrapped by proxies which log invoked methods, their arguments and results.

Other than that you can configure basics.
 - Where to send logs (file, console).
 - What contextual data to log (thread, timestamp).
 - How to turn objects into string (other than invoking `toString`).

Before we learn how to take benefit of automatically generated logs, let's learn how to configure basics.

### Renderer

Consider this minimal example.

    Logger logger = consoleLogger(new TextRenderer());
    logger.log(message("content"));
    -------------- prints --------------
    content

It creates a `Logger` which represents a place where logs are sent, in our case it's console.
Writing objects to console requires converting them to `String`, that's why `ConsoleLogger` needs `Renderer<String>`.
Since `"content"` is already  a `String`, `TextRenderer` implementation just returns it.

In addition to content, `Message` can have attributes.

    Logger logger = consoleLogger(new TextRenderer());
    logger.log(message("content").attribute(Instant.now()));
    -------------- prints --------------
    2017-06-28T16:03:33.421Z  content

`TextRenderer` renders message attributes on left side to content.

`Message` content can be any `Object`.
For example if it is an array, `TextRenderer` will use `Arrays.toString()` for rendering.

    Logger logger = consoleLogger(new TextRenderer());
    logger.log(message(new int[] { 1, 2, 3 }));
    -------------- prints --------------
    [1, 2, 3]

If you watch produced logs in browser, then render them as html.

    Logger logger = consoleLogger(new HtmlRenderer());
    logger.log(message("content"));
    -------------- prints --------------
    <span style="display: block; white-space: nowrap; font-family: monospace;">content</span>

### Logger

[destination](#destination) | [attributes](#attributes) | [safety](#safety)

##### Destination

`Logger` is a sink where messages goes into.
There are several predefined destinations where logs can go.

`ConsoleLogger` prints to standard output (`System.out`).

    Logger logger = consoleLogger(new TextRenderer());

`FileLogger` prints to file.
It can be used with text or html renderer depending what kind of file you want.

    Logger textLogger = fileLogger(Paths.get("log.txt"), new TextRenderer());
    Logger htmlLogger = fileLogger(Paths.get("log.html"), new HtmlRenderer());
    
`BrowserLogger` prints to new tab in browser using linux `bcat`.
It works best when used with `HtmlRenderer`.

    Logger logger = browserLogger(new HtmlRenderer());

`WritingLogger` allows you to specify your own `Writer`.
Actually `ConsoleLogger`, `FileLogger` and `BrowserLogger` use it internally.
Let's say you want to send all logs to `System.err`.

    Charset charset = Charset.forName("utf8");
    Writer writer = new OutputStreamWriter(System.err);
    Logger logger = logger(writer, new TextRenderer());

`NoLogger` just swallows logs without saving them anywhere.

    Logger logger = noLogger();

`ComposedLogger` sends messages to more than one destination.

    Logger logger = compose(
        consoleLogger(new TextRenderer()),
        fileLogger(Paths.get("log.html"), new HtmlRenderer()));

##### Attributes

Some loggers can add extra attributes to each passing `Message`. 

`TimeLogger` adds `ZonedDateTime` attribute to `Message` so you don't have to add it manually.

    Logger logger = consoleLogger(new TextRenderer());
    logger.log(message("content").attribute(Instant.now()));
    -------------- prints --------------
    2017-06-28T16:03:33.421Z  content

    Logger logger = time(Clock.systemUTC(), consoleLogger(new TextRenderer()));
    logger.log(message("content"));
    -------------- prints --------------
    2017-06-29T15:13:16.575Z  content

`ThreadLogger` adds `Thread.currentThread()` attribute.

    Logger logger = thread(consoleLogger(new TextRenderer()));
    logger.log(message("content"));
    -------------- prints --------------
    Thread(main)  content

##### Safety

By default loggers work only in single-threaded environment.
There are several utilities to solve it.

`SynchronizedLogger` wraps another `Logger` and makes `log(Message)` synchronized.
This makes `Logger` thread-safe.

    Logger logger = synchronize(consoleLogger(new TextRenderer()));

`AsynchronousLogger` stores `Message` in queue and returns immediately.
This `Message` is passed to wrapped `Logger` by another dedicated thread.
This makes `Logger` thread-safe.
It also improves parallelism by not blocking production threads.

    Logger logger = asynchronous(consoleLogger(new TextRenderer()));

`Fuse` prevents `StackOverflowException` during rendering.
Logging `Message` causes it to be rendered.
Rendering usually invokes `toString` or other methods on various objects.
If any of those methods trigger log event, then you may end up with infinite recursion.
Wrapping `Logger` with `Fuse` prevents it.

    Logger logger = fuse().install(consoleLogger(new TextRenderer()));

`Fuse` prevents same thread to log another `Message` if that thread it is already rendering one.
If you use `AsynchronousLogger` then logging and rendering happens in different threads.
Both threads need to pass through same `Fuse`.

    Fuse fuse = fuse();
    Logger logger = fuse.install(asynchronous(fuse.install(consoleLogger(new TextRenderer()))));

`CatchingLogger` handles errors during logging.
Since rendering invokes methods on alien objects, any of those can throw exception.
`CatchingLogger` catches exception and logs it as `Message`.
If that also fails, it prints exception's stack trace to `System.err`.

    Logger logger = catching(consoleLogger(new TextRenderer()));
    logger.log(message(new Object() {
      public String toString() {
        throw new RuntimeException("failed");
      }
    }));
    -------------- prints --------------
    org.logbuddy.LogBuddyException: java.lang.RuntimeException: failed

# Decorator

`Decorator` allows you to automate logging.
It's an interface that takes any object and wraps it in logging proxy.

    public interface Decorator {
      public <T> T decorate(T decorable);
    }

`InvocationDecorator` wraps object in proxy that logs invocations.
When method is called on proxy, it logs `Message` with instance, method name and arguments.
When method exits, it logs returned result.

    Decorator decorator = invocationDecorator(consoleLogger(new TextRenderer()));
    List<String> list = decorator.decorate(new ArrayList<>(asList("a", "b", "c")));
    list.contains("x");
    -------------- prints --------------
    List[a, b, c].contains(x)
    returned false

It also logs if method thrown exception.

    Decorator decorator = invocationDecorator(consoleLogger(new TextRenderer()));
    List<String> list = decorator.decorate(new ArrayList<>(asList("a", "b", "c")));
    list.remove(5);
    -------------- prints --------------
    List[a, b, c].remove(5)
    thrown java.lang.IndexOutOfBoundsException: Index: 5, Size: 3
        at java.util.ArrayList.rangeCheck(ArrayList.java:653)
        at java.util.ArrayList.remove(ArrayList.java:492)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.lang.reflect.Method.invoke(Method.java:498)
        at org.logbuddy.decorator.InvocationDecorator$DecorateHandler.handle(InvocationDecorator.java:76)
        at net.bytebuddy.renamed.java.util.ArrayList$ByteBuddy$1ounHJhI.remove(Unknown Source)
        at Documentation.decorator_invocation_thrown(Documentation.java:143)
        at Documentation.main(Documentation.java:36)


`InvocationDepthLogger` tracks messages produced by `InvocationDecorator`.
It increments/decrements stack trace depth counter on entrance/exit of methods and adds `InvocationDepth` attribute.
This attribute is recognized by `TextRenderer` and rendered as intendation which indicates stack trace depth.

    Logger logger = fuse().install(invocationDepth(consoleLogger(new TextRenderer())));
    Decorator decorator = invocationDecorator(logger);
    Color red = decorator.decorate(Color.RED);
    Color green = decorator.decorate(Color.GREEN);
    List<Object> list = decorator.decorate(new ArrayList<>(asList(red, green)));
    list.contains(green);
    -------------- prints --------------
      List[java.awt.Color[r=255,g=0,b=0], java.awt.Color[r=0,g=255,b=0]].contains(java.awt.Color[r=0,g=255,b=0])
        java.awt.Color[r=0,g=255,b=0].equals(java.awt.Color[r=255,g=0,b=0])
          java.awt.Color[r=255,g=0,b=0].getRGB()
          returned -65536
        returned false
        java.awt.Color[r=0,g=255,b=0].equals(java.awt.Color[r=0,g=255,b=0])
          java.awt.Color[r=0,g=255,b=0].getRGB()
          returned -16711936
        returned true
      returned true

`JdkDecorator` helps you decorate non-public classes from `java.` package. When ByteBuddy creates proxy for an object of non-public class, it defines proxy class in the same package as non-public class in order to access it. This is not possible for classes from `java.` package due to security checks. ByteBuddy is forced to define class in different package which makes superclass invisible.

```
Logger logger = consoleLogger(new TextRenderer());
Decorator decorator = invocationDecorator(logger);
List<String> decorable = Arrays.asList("string");
decorator.decorate(decorable);

-------------- prints --------------

Exception in thread "main" java.lang.IllegalStateException: Invisible super type class java.util.Arrays$ArrayList for class net.bytebuddy.renamed.java.util.Arrays$ArrayList$ByteBuddy$RBH9CXJA
	at net.bytebuddy.dynamic.scaffold.InstrumentedType$Default.validated(InstrumentedType.java:1566)
	at net.bytebuddy.dynamic.scaffold.MethodRegistry$Default.prepare(MethodRegistry.java:519)
	at net.bytebuddy.dynamic.scaffold.subclass.SubclassDynamicTypeBuilder.toTypeWriter(SubclassDynamicTypeBuilder.java:212)
	at net.bytebuddy.dynamic.scaffold.subclass.SubclassDynamicTypeBuilder.toTypeWriter(SubclassDynamicTypeBuilder.java:203)
	at net.bytebuddy.dynamic.DynamicType$Builder$AbstractBase$UsingTypeWriter.make(DynamicType.java:4055)
	at net.bytebuddy.dynamic.DynamicType$Builder$AbstractBase.make(DynamicType.java:3739)
	at net.bytebuddy.dynamic.DynamicType$Builder$AbstractBase$Delegator.make(DynamicType.java:3991)
	at org.logbuddy.decorator.InvocationDecorator.decorate(InvocationDecorator.java:49)
	at Documentation.decorator_jdk_fail(Documentation.java:224)
	at Documentation.main(Documentation.java:53)
```

`JdkDecorator` solves it by wrapping object being decorated in extra proxy first, before delegating decoration to another `Decorator`. This proxy, instead of being of non-public type, is defined as subclass of nearest public superclass and implements interfaces that were peeled of from non-public classes.

For example `Arrays.asList("")` returns instance of non-public class `java.util.Arrays$ArrayList`. `JdkDecorator` wraps it in proxy extending `AbstractList` and implementing interfaces `RandomAccess` and `Serializable`.

    Logger logger = consoleLogger(new TextRenderer());
    Decorator decorator = jdk(invocationDecorator(logger));
    List<String> decorable = Arrays.asList("string");
    List<String> decorated = decorator.decorate(decorable);
    decorated.get(0);
    -------------- prints --------------
    List[string].get(0)
    returned string

This works as long as you cast that proxy only to public superclass and peeled interfaces. Trying to cast proxy to original non-public class causes `ClassCastException`.

`ComponentsDecorator` allows you to decorate fields of an object and elements of an array. It uses reflection to read value of each field/element, decorate it using provided `Decorator` and sets it back.

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
    decorator.decorate(service).toString();
    -------------- prints --------------
    java.awt.Color[r=255,g=0,b=0].toString()
    returned java.awt.Color[r=255,g=0,b=0]
    java.awt.Color[r=0,g=255,b=0].toString()
    returned java.awt.Color[r=0,g=255,b=0]
    java.awt.Color[r=0,g=0,b=255].toString()
    returned java.awt.Color[r=0,g=0,b=255]

`TraversingDecorator` helps you decorate whole dependency graph at once.
It will crawl through all instances reachable from original instance.
Those instance's fields will be injected with decorated instances.
Original instance is also decorated.

Let's assume all dependencies are reachable through `app` instance.

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
    -------------- prints --------------
      App.start()
        Service#1521118594.serve()
        returned
        Service#1682463303.serve()
        returned
      returned

Optionally, you can restrict this recursion and skip some fields by providing `Predicate<Field>`.

    traversing(decorator)
        .filter(field -> !field.getType().isArray());

`CachingDecorator` remembers objects you already decorated.
If you decorate same object again, it returns same result as first time.

    Decorator cachingDecorator = caching(decorator);
    assertSame(
        cachingDecorator.decorate(object),
        cachingDecorator.decorate(object));

`InjectingDecorator` allows you to manually log messages in production code.
It injects given `Logger` to fields of that type.
It is wise to initialize this field with instance of `NoLogger` to prevent `NullPointerException` in case instance is not decorated.

    class Service {
      Logger logger = noLogger();

      public void serve() {
        logger.log(message("adhoc message"));
      }
    }

    Logger logger = consoleLogger(new TextRenderer());
    Decorator decorator = injecting(logger);

    decorator.decorate(new Service()).serve();
    -------------- prints --------------
    adhoc message

`ComposedDecorator` lets you combine two decorators into one.
For example you want to combine functionality of `InvocationDecorator` with `InjectingLoggerDecorator`.

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
    -------------- prints --------------
      Service#1920387277.serve()
        adhoc message
      returned


# Customization

[rendering](#rendering)

### Rendering

To change how objects are turned into `String`, implement `Renderer<String>` interface or extend `TextRenderer`.

    public interface Renderer<T> {
      public T render(Object model);
    }

Let's say you think `java.awt.Color.toString()` it too verbose.

    TextRenderer renderer = new TextRenderer();
    Logger logger = consoleLogger(renderer);
    logger.log(message(Color.RED));
    -------------- prints --------------
    java.awt.Color[r=255,g=0,b=0]

You can override `render` method to change `TextRenderer` behavior .

    TextRenderer renderer = new TextRenderer() {
      public String render(Object model) {
        return model instanceof Color
            ? format("Color(#%06X)", ((Color) model).getRGB() & 0x00FFFFFF)
            : super.render(model);
      }
    };
    Logger logger = consoleLogger(renderer);
    logger.log(message(Color.GREEN));
    -------------- prints --------------
    Color(#00FF00)

# Integration

Let's assume you already use logbuddy for logging events in your app.
Then, your project adds a library that uses different logging mechanism.

[stdio](#stdio) | [slf4j](#slf4j)

### Stdio

Some libraries uses simplest form of logging by using stdio (especially stderr).
You can redirect those streams to merge with your other logbuddy logs.

    Logger logger = consoleLogger(new TextRenderer());
    stdioBinder(Charset.forName("utf8"), logger).bind();

    logger.log(message("message"));
    System.out.println("message");
    System.err.println("message");
    -------------- prints --------------
    message
    [stdout] message
    [stderr] message

### Slf4j

[slf4j](https://www.slf4j.org/) provides single abstract facade for logging.
Libraries which log are dependent only to this facade.
Project that includes those libraries chooses one of many implementations by adding corresponding jar.

Now you added one of those jars to your project.
Your app is using logbuddy.
You want to merge logs from library with logs from your app.
You can do this by adding `logbuddy-slf4j-???.jar` to your project.
It provides slf4j implementation that binds to facade.
At the startup of your application you must configure slf4j by providing destination `Logger` and logging `Level`.

    Logger logger = consoleLogger(new TextRenderer());
    slf4jBinder(Level.INFO, logger).bind();
