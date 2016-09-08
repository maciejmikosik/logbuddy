package org.logbuddy.renderer;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.logbuddy.renderer.Html.html;

import java.util.List;
import java.util.Random;

public class Charts {
  private static final Random random = new Random();

  public static Html plot(List<? extends Number> values, int height) {
    List<Double> doubles = values.stream()
        .map(number -> number.doubleValue())
        .collect(toList());
    return plotDoubles(doubles, height);
  }

  private static Html plotDoubles(List<Double> values, int height) {
    String canvasId = format("canvas_%s", random.nextInt(Integer.MAX_VALUE));
    double min = values
        .stream()
        .min(Double::compare)
        .get();
    double max = values
        .stream()
        .max(Double::compare)
        .get();
    List<Integer> dots = values.stream()
        .map(value -> (int) (phase(min, value, max) * height))
        .collect(toList());

    Html canvas = canvasElement(canvasId, values.size(), height + 1);
    Html drawingScript = drawingScript(canvasId, dots, height);
    Html axis = min * max < 0
        ? axis(canvasId, phase(min, 0, max), height)
        : html("");
    return html(canvas.body + drawingScript.body + axis.body);
  }

  private static Html axis(String canvasId, double phase, int height) {
    int axisY = (int) ((1 - phase) * height);
    return html(format(""
        + "<script>\n"
        + "var canvas = document.getElementById('%s');\n"
        + "var context = canvas.getContext('2d');\n"
        + "context.moveTo(0, %s);\n"
        + "context.lineTo(2000, %s);\n"
        + "context.strokeStyle = '#808080';\n"
        + "context.stroke();\n"
        + "</script>",
        canvasId, axisY, axisY));
  }

  private static Html canvasElement(String canvasId, int width, int height) {
    return html(format(""
        + "<canvas id='%s' width='%s' height='%s' style='border:1px solid #c3c3c3;'>"
        + "Your browser does not support the HTML5 canvas tag."
        + "</canvas>",
        canvasId, width, height));
  }

  private static Html drawingScript(String canvasId, List<Integer> dots, int height) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < dots.size(); i++) {
      builder.append(format("context.fillRect(%s, %s, 3, 3)\n", i, height - 1 - dots.get(i)));
    }
    return html(format(""
        + "<script>\n"
        + "var canvas = document.getElementById('%s');\n"
        + "var context = canvas.getContext('2d');\n"
        + "context.fillStyle = '#000000';\n"
        + "%s"
        + "</script>",
        canvasId, builder.toString()));
  }

  private static double phase(double begin, double value, double end) {
    return (value - begin) / (end - begin);
  }
}
