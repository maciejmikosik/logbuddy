package org.logbuddy.renderer.gallery;

import static java.lang.String.format;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.Optional;

import javax.imageio.ImageIO;

import org.logbuddy.LogBuddyException;

public class Gallery {
  private final Optional<Integer> width;
  private final Optional<Integer> height;

  private Gallery(Optional<Integer> width, Optional<Integer> height) {
    this.width = width;
    this.height = height;
  }

  public static Gallery gallery() {
    return new Gallery(Optional.empty(), Optional.empty());
  }

  public Gallery width(int width) {
    return new Gallery(Optional.of(width), height);
  }

  public Gallery height(int height) {
    return new Gallery(width, Optional.of(height));
  }

  public String paint(RenderedImage image) {
    return paint(encode("jpeg", image));
  }

  public String paint(byte[] image) {
    String base64Image = utf8String(Base64.getEncoder().encode(image));
    String openImageInNewTab = format(""
        + "var w = window.open(); "
        + "w.document.write('<img src=data:/image;base64,%s />'); "
        + "w.document.close();",
        base64Image);
    StringBuilder builder = new StringBuilder();
    builder.append("<img");
    width.ifPresent(width -> builder.append(format(" width=%s", width)));
    height.ifPresent(height -> builder.append(format(" height=%s", height)));
    builder.append(format(" src=data:/image;base64,%s", base64Image));
    builder.append(format(" onclick=\"%s\"", openImageInNewTab));
    builder.append("/>");
    return builder.toString();
  }

  private static byte[] encode(String format, RenderedImage image) {
    try {
      ByteArrayOutputStream stream = new ByteArrayOutputStream();
      ImageIO.write(image, format, stream);
      return stream.toByteArray();
    } catch (IOException e) {
      throw new LogBuddyException(e);
    }
  }

  private static String utf8String(byte[] bytes) {
    return new String(bytes, Charset.forName("utf8"));
  }
}
