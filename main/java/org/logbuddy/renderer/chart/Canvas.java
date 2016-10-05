package org.logbuddy.renderer.chart;

import static java.lang.String.format;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

public class Canvas {
  private static AtomicInteger idGenerator = new AtomicInteger();

  public final String id;
  public final int width;
  public final int height;

  private final StringBuilder script = new StringBuilder();

  public Canvas(String id, int width, int height) {
    this.id = id;
    this.width = width;
    this.height = height;
  }

  public static Canvas canvas(int width, int height) {
    String id = format("canvas_%s", idGenerator.incrementAndGet());
    return new Canvas(id, width, height);
  }

  public String toHtml() {
    StringBuilder builder = new StringBuilder();
    builder.append(format("<canvas id='%s' width='%s' height='%s' style='border:1px solid #c3c3c3;'>",
        id, width, height));
    builder.append("Your browser does not support the HTML5 canvas tag.");
    builder.append("</canvas>");
    builder.append("<script>");
    builder.append(format("var canvas = document.getElementById('%s');\n", id));
    builder.append("var context = canvas.getContext('2d');\n");
    builder.append(script);
    builder.append("</script>");
    return builder.toString();
  }

  public Canvas beginPath() {
    script.append("context.beginPath();\n");
    return this;
  }

  public Canvas moveTo(double x, double y) {
    script.append(format("context.moveTo(%s, %s);\n", x, y));
    return this;
  }

  public Canvas lineTo(double x, double y) {
    script.append(format("context.lineTo(%s, %s);\n", x, y));
    return this;
  }

  public Canvas lineWidth(double width) {
    script.append(format("context.lineWidth = %s;\n", width));
    return this;
  }

  public Canvas strokeStyle(Color color) {
    script.append(format("context.strokeStyle = '%s';\n", hex(color)));
    return this;
  }

  public Canvas stroke() {
    script.append("context.stroke();\n");
    return this;
  }

  public Canvas fillStyle(Color color) {
    script.append(format("context.fillStyle = '%s';\n", hex(color)));
    return this;
  }

  public Canvas fillRect(double x, double y, double width, double height) {
    script.append(format("context.fillRect(%s, %s, %s, %s);\n", x, y, width, height));
    return this;
  }

  private static String hex(Color color) {
    return format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
  }
}
