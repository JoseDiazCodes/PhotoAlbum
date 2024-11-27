package photoalbum.view;

import photoalbum.model.IPhotoAlbum;
import photoalbum.model.IShape;
import photoalbum.model.ISnapshot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An HTML-based view implementation for the photo album application. This class generates
 * a static HTML file containing SVG representations of all snapshots.
 */
public class WebView implements IPhotoAlbumView {

  /**
   * The photo album model containing all shapes and snapshots to be rendered. This model
   * provides the data that will be converted to HTML/SVG.
   */
  private final IPhotoAlbum model;

  /**
   * The path where the generated HTML file will be saved. This file will contain all
   * snapshots rendered as SVG elements.
   */
  private final String outputFile;

  /**
   * The width of each SVG canvas in pixels. This determines how much horizontal space
   * is available for rendering shapes.
   */
  private final int width;

  /**
   * The height of each SVG canvas in pixels. This determines how much vertical space
   * is available for rendering shapes.
   */
  private final int height;

  /**
   * Creates a new WebView with the specified model, output path, and dimensions.
   *
   * @param model The photo album model containing shapes and snapshots to display
   * @param outputFile The path where the HTML file will be saved
   * @param width The width of each SVG canvas in pixels
   * @param height The height of each SVG canvas in pixels
   */

  public WebView(IPhotoAlbum model, String outputFile, int width, int height) {
    this.model = model;
    this.outputFile = outputFile;
    this.width = width;
    this.height = height;
  }

  /**
   * Generates the complete HTML file containing all snapshots and saves it to the
   * specified output path. This is the main entry point for creating the web view.
   */
  @Override
  public void display() {
    StringBuilder html = new StringBuilder();
    generateHeader(html);
    for (ISnapshot snapshot : model.getSnapshots()) {
      generateSnapshot(html, snapshot);
    }
    html.append("</body>\n</html>");

    try {
      java.nio.file.Files.writeString(java.nio.file.Path.of(outputFile), html.toString());
    } catch (IOException e) {
      System.err.println("Error writing HTML output: " + e.getMessage());
    }
  }

  /**
   * Generates the HTML header including CSS styles for snapshot presentation. This
   * defines the visual appearance of the web page.
   *
   * @param html The StringBuilder containing the HTML document being generated
   */
  private void generateHeader(StringBuilder html) {
    html.append("<!DOCTYPE html>\n<html>\n<head>\n")
            .append("<style>\n")
            .append("body { background: white; margin: 0; padding: 20px; }\n")
            .append(".snapshot { \n")
            .append("    background-color: rgb(173, 216, 230); \n")
            .append("    border: 6px solid red; \n")
            .append("    padding: 10px; \n")
            .append("    margin-bottom: 20px; \n")
            .append("    width: fit-content; \n")
            .append("}\n")
            .append("svg { display: block; margin: 0; }\n")
            .append("h2 { margin: 0 0 10px 0; }\n")
            .append("p { margin: 0 0 10px 0; }\n")
            .append("</style>\n")
            .append("</head>\n<body>\n");
  }

  /**
   * Generates an HTML/SVG representation of a single snapshot. This includes the
   * snapshot's metadata and all shapes in their correct positions.
   *
   * @param html The StringBuilder containing the HTML document being generated
   * @param snapshot The snapshot to be rendered as SVG
   */
  private void generateSnapshot(StringBuilder html, ISnapshot snapshot) {
    html.append("<div class='snapshot'>\n")
            .append("<h2>").append(snapshot.getId()).append("</h2>\n");
    if (!snapshot.getDescription().isEmpty()) {
      html.append("<p>Description: ").append(snapshot.getDescription()).append("</p>\n");
    }
    html.append(String.format("<svg width='%d' height='%d'>\n", width, height));

    List<IShape> shapes = new ArrayList<>(snapshot.getShapes());
    Collections.sort(shapes, (a, b) -> getLayerPriority(a) - getLayerPriority(b));

    for (IShape shape : shapes) {
      generateShape(html, shape);
    }

    html.append("</svg>\n</div>\n");
  }

  /**
   * Determines the rendering priority of a shape based on its type and name. Lower
   * numbers are rendered first, ensuring proper layering of elements.
   *
   * @param shape The shape whose rendering priority is being determined
   * @return An integer priority value, where 0 is rendered first
   */
  private int getLayerPriority(IShape shape) {
    String name = shape.getName().toLowerCase();

    // Background elements (sky, ground, canvas backgrounds)
    if (name.contains("background") || name.contains("sky")
            || name.contains("ground") || name.equals("rect1")) {
      return 0;
    }

    // Court or playing field
    if (name.contains("court")) {
      return 1;
    }

    // Main structures (buildings, hoops)
    if (name.startsWith("b") || name.contains("hoop")
            || name.contains("board") || name.contains("rim")) {
      return 2;
    }

    // Windows and details
    if (name.startsWith("window") || name.contains("net")) {
      return 3;
    }

    // Moving objects and decorations
    if (name.contains("ball") || name.equals("moon")
            || name.startsWith("circle")) {
      return 4;
    }

    // Default priority for any other shapes
    return 5;
  }

  /**
   * Generates the SVG element for a specific shape based on its type. This method
   * delegates to type-specific generators.
   *
   * @param html The StringBuilder containing the HTML document being generated
   * @param shape The shape to be converted to SVG
   */
  private void generateShape(StringBuilder html, IShape shape) {
    try {
      if (shape.getType().equals("rectangle")) {
        generateRectangle(html, shape);
      } else if (shape.getType().equals("oval")) {
        generateOval(html, shape);
      }
    } catch (Exception e) {
      System.err.println("Error generating shape: " + shape.getName());
    }
  }

  /**
   * Generates SVG markup for a rectangle shape. This includes extracting coordinates,
   * dimensions, and color information.
   *
   * @param html The StringBuilder containing the HTML document being generated
   * @param shape The rectangle shape to be converted to SVG
   */
  private void generateRectangle(StringBuilder html, IShape shape) {
    String coords = shape.toString().split("\n")[2];
    double x = Double.parseDouble(coords.substring(coords.indexOf("(") + 1,
            coords.indexOf(",")).trim());
    double y = Double.parseDouble(coords.substring(coords.indexOf(",") + 1,
            coords.indexOf(")")).trim());
    double width = Double.parseDouble(coords.substring(coords.indexOf("Width:") + 6,
            coords.indexOf(",", coords.indexOf("Width:"))).trim());
    double height = Double.parseDouble(coords.substring(coords.indexOf("Height:") + 7,
            coords.indexOf(",", coords.indexOf("Height:"))).trim());
    String color = getColor(shape);

    html.append(String.format("<rect x='%.2f' y='%.2f' width='%.2f' height='%.2f' fill='%s' />\n",
            x, y, width, height, color));
  }

  /**
   * Generates SVG markup for an oval shape. This includes extracting center point,
   * radii, and color information.
   *
   * @param html The StringBuilder containing the HTML document being generated
   * @param shape The oval shape to be converted to SVG
   */
  private void generateOval(StringBuilder html, IShape shape) {
    String coords = shape.toString().split("\n")[2];
    double cx = Double.parseDouble(coords.substring(coords.indexOf("(") + 1,
            coords.indexOf(",")).trim());
    double cy = Double.parseDouble(coords.substring(coords.indexOf(",") + 1,
            coords.indexOf(")")).trim());
    double rx = Double.parseDouble(coords.substring(coords.indexOf("X radius:") + 9,
            coords.indexOf(",", coords.indexOf("X radius:"))).trim());
    double ry = Double.parseDouble(coords.substring(coords.indexOf("Y radius:") + 9,
            coords.indexOf(",", coords.indexOf("Y radius:"))).trim());
    String color = getColor(shape);

    html.append(String.format("<ellipse cx='%.2f' cy='%.2f' rx='%.2f' ry='%.2f' fill='%s' />\n",
            cx, cy, rx, ry, color));
  }

  /**
   * Extracts the color information from a shape and converts it to an RGB string
   * suitable for SVG. This handles the conversion from float values to RGB format.
   *
   * @param shape The shape whose color information is being extracted
   * @return A string in the format "rgb(r,g,b)" where r,g,b are integers 0-255
   */
  private String getColor(IShape shape) {
    String colorStr = shape.toString();
    int colorStart = colorStr.lastIndexOf("Color: (") + 8;
    int colorEnd = colorStr.lastIndexOf(")");
    String[] rgb = colorStr.substring(colorStart, colorEnd).split(",");

    int r = (int)(Float.parseFloat(rgb[0].trim()) * 255);
    int g = (int)(Float.parseFloat(rgb[1].trim()) * 255);
    int b = (int)(Float.parseFloat(rgb[2].trim()) * 255);

    return String.format("rgb(%d,%d,%d)", r, g, b);
  }
}
