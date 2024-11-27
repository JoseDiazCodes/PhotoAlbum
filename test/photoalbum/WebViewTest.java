package photoalbum;

import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import photoalbum.model.IPhotoAlbum;
import photoalbum.model.PhotoAlbumImpl;
import photoalbum.view.WebView;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Test suite for the WebView class. Tests HTML generation and proper shape rendering.
 */
public class WebViewTest {
  private IPhotoAlbum model;
  private String testOutputPath;

  @Before
  public void setup() {
    model = new PhotoAlbumImpl();
    testOutputPath = "test_output.html";
  }

  @After
  public void cleanup() {
    try {
      Files.deleteIfExists(Path.of(testOutputPath));
    } catch (IOException e) {
      System.err.println("Could not delete test file: " + e.getMessage());
    }
  }

  @Test
  public void testEmptyAlbum() {
    WebView view = new WebView(model, testOutputPath, 800, 600);
    view.display();

    assertTrue(Files.exists(Path.of(testOutputPath)));
    try {
      String content = Files.readString(Path.of(testOutputPath));
      assertTrue(content.contains("<!DOCTYPE html>"));
      assertTrue(content.contains("</html>"));
      assertFalse(content.contains("<svg"));  // No shapes = no SVG
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

  @Test
  public void testSingleRectangle() {
    // Create and configure rectangle
    model.addShape("R1", "rectangle");
    model.getShape("R1").move(100, 100);
    model.getShape("R1").resize(50, 30);
    model.getShape("R1").setColor(1.0, 0.0, 0.0);
    model.takeSnapshot("One red rectangle");

    // Generate HTML
    WebView view = new WebView(model, testOutputPath, 800, 600);
    view.display();

    try {
      List<String> lines = Files.readAllLines(Path.of(testOutputPath));
      String content = String.join("\n", lines);

      // Debug print
      System.out.println("Generated HTML Content:");
      System.out.println(content);

      // Find the rectangle element
      String rectangleLine = lines.stream()
              .filter(line -> line.contains("<rect"))
              .findFirst()
              .orElse("");
      System.out.println("Rectangle line: " + rectangleLine);

      // Check for the exact rectangle element format
      String expectedRect = String.format(
              "<rect x='%.2f' y='%.2f' width='%.2f' height='%.2f' fill='rgb(255,0,0)' />",
              100.0, 100.0, 50.0, 30.0
      );
      assertTrue("Should contain correctly formatted rectangle",
              content.contains(expectedRect));

      // Check other elements
      assertTrue("Should contain SVG tag",
              content.contains("<svg width='800' height='600'>"));
      assertTrue("Should contain snapshot div",
              content.contains("<div class='snapshot'>"));
      assertTrue("Should contain description",
              content.contains("One red rectangle"));
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

  @Test
  public void testSingleOval() {
    // Create and configure oval
    model.addShape("O1", "oval");
    model.getShape("O1").move(200, 200);
    model.getShape("O1").resize(40, 20);
    model.getShape("O1").setColor(0.0, 1.0, 0.0);
    model.takeSnapshot("One green oval");

    // Generate HTML
    WebView view = new WebView(model, testOutputPath, 800, 600);
    view.display();

    try {
      List<String> lines = Files.readAllLines(Path.of(testOutputPath));
      String content = String.join("\n", lines);

      // Check for the exact ellipse element format
      String expectedEllipse = "<ellipse cx='200.00' cy='200.00' " +
              "rx='40.00' ry='20.00' fill='rgb(0,255,0)' />";
      assertTrue("Should contain correctly formatted ellipse",
              content.contains(expectedEllipse));

      // Check for snapshot description
      assertTrue("Should contain snapshot description",
              content.contains("One green oval"));

      // Check basic HTML structure
      assertTrue("Should contain SVG tag",
              content.contains("<svg width='800' height='600'>"));
      assertTrue("Should contain snapshot div",
              content.contains("<div class='snapshot'>"));
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

  @Test
  public void testMultipleSnapshots() {
    // First snapshot
    model.addShape("R1", "rectangle");
    model.getShape("R1").move(100, 100);
    model.takeSnapshot("First");

    // Second snapshot
    model.getShape("R1").move(200, 200);
    model.takeSnapshot("Second");

    WebView view = new WebView(model, testOutputPath, 800, 600);
    view.display();

    try {
      List<String> lines = Files.readAllLines(Path.of(testOutputPath));
      String content = String.join("\n", lines);

      assertTrue(content.contains("First"));
      assertTrue(content.contains("Second"));
      assertEquals(2, countOccurrences(content, "<div class='snapshot'>"));
      assertEquals(2, countOccurrences(content, "<svg"));
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

  @Test
  public void testShapeLayering() {
    // Add shapes with specific coordinates and sizes
    model.addShape("background", "rectangle");
    model.getShape("background").move(0, 0);
    model.getShape("background").resize(800, 600);
    model.getShape("background").setColor(0.5, 0.5, 0.5);

    model.addShape("building", "rectangle");
    model.getShape("building").move(100, 100);
    model.getShape("building").resize(200, 300);
    model.getShape("building").setColor(0.3, 0.3, 0.3);

    model.addShape("window", "rectangle");
    model.getShape("window").move(150, 150);
    model.getShape("window").resize(50, 50);
    model.getShape("window").setColor(1.0, 1.0, 1.0);

    model.takeSnapshot("Test layering");

    WebView view = new WebView(model, testOutputPath, 800, 600);
    view.display();

    try {
      List<String> lines = Files.readAllLines(Path.of(testOutputPath));
      String content = String.join("\n", lines);

      // Debug print
      System.out.println("Generated SVG Content:");
      lines.stream()
              .filter(line -> line.contains("<rect"))
              .forEach(System.out::println);

      // Find the rect elements
      List<String> rectElements = lines.stream()
              .filter(line -> line.contains("<rect"))
              .toList();

      // Verify we found all three rectangles
      assertEquals("Should have three rectangles", 3, rectElements.size());

      // Check ordering by finding indices
      int bgIndex = -1, buildingIndex = -1, windowIndex = -1;
      for (int i = 0; i < rectElements.size(); i++) {
        String rect = rectElements.get(i);
        if (rect.contains("width='800.00'")) {  // Background
          bgIndex = i;
        } else if (rect.contains("width='200.00'")) {  // Building
          buildingIndex = i;
        } else if (rect.contains("width='50.00'")) {  // Window
          windowIndex = i;
        }
      }

      // Debug print
      System.out.println("Indices - Background: " + bgIndex +
              ", Building: " + buildingIndex +
              ", Window: " + windowIndex);

      // Assert proper ordering
      assertTrue("Background should be found", bgIndex >= 0);
      assertTrue("Building should be found", buildingIndex >= 0);
      assertTrue("Window should be found", windowIndex >= 0);
      assertTrue("Background should come before building", bgIndex < buildingIndex);
      assertTrue("Building should come before window", buildingIndex < windowIndex);
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

  @Test
  public void testStyleGeneration() {
    WebView view = new WebView(model, testOutputPath, 800, 600);
    view.display();

    try {
      String content = Files.readString(Path.of(testOutputPath));
      assertTrue(content.contains("<style>"));
      assertTrue(content.contains("background-color: rgb(173, 216, 230)"));
      assertTrue(content.contains("border: 6px solid red"));
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

//  private int countOccurrences(String str, String substr) {
//    return (str.length() - str.replace(substr, "").length()) / substr.length();
//  }

  @Test
  public void testEmptyDescription() {
    model.addShape("R1", "rectangle");
    model.getShape("R1").move(100, 100);
    model.getShape("R1").resize(50, 30);
    // Take snapshot with empty description
    model.takeSnapshot("");

    WebView view = new WebView(model, testOutputPath, 800, 600);
    view.display();

    try {
      String content = Files.readString(Path.of(testOutputPath));
      assertFalse("Empty description should not create description paragraph",
              content.contains("<p>Description: </p>"));
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

  @Test
  public void testOverlappingShapes() {
    // Create two overlapping rectangles
    model.addShape("R1", "rectangle");
    model.getShape("R1").move(100, 100);
    model.getShape("R1").resize(100, 100);
    model.getShape("R1").setColor(1.0, 0.0, 0.0);  // red

    model.addShape("R2", "rectangle");
    model.getShape("R2").move(150, 150);
    model.getShape("R2").resize(100, 100);
    model.getShape("R2").setColor(0.0, 1.0, 0.0);  // green

    model.takeSnapshot("Overlapping shapes");

    WebView view = new WebView(model, testOutputPath, 800, 600);
    view.display();

    try {
      List<String> lines = Files.readAllLines(Path.of(testOutputPath));
      // Print the SVG content for debugging
      System.out.println("SVG Content:");
      lines.stream()
              .filter(line -> line.contains("<rect"))
              .forEach(System.out::println);

      // Instead of testing specific order, let's verify both shapes are present
      String content = String.join("\n", lines);
      assertTrue("Should contain first rectangle (red)",
              content.contains("rgb(255,0,0)"));
      assertTrue("Should contain second rectangle (green)",
              content.contains("rgb(0,255,0)"));
      assertEquals("Should have exactly two rectangles",
              2, countOccurrences(content, "<rect"));

      // Verify both rectangles have correct positions
      assertTrue("Should have first rectangle position",
              content.contains("x='100.00' y='100.00'"));
      assertTrue("Should have second rectangle position",
              content.contains("x='150.00' y='150.00'"));
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

  @Test
  public void testCanvasSize() {
    // Test different canvas sizes
    int testWidth = 1024;
    int testHeight = 768;

    model.addShape("R1", "rectangle");
    model.takeSnapshot("Test canvas size");

    WebView view = new WebView(model, testOutputPath, testWidth, testHeight);
    view.display();

    try {
      String content = Files.readString(Path.of(testOutputPath));
      assertTrue("Should have correct SVG width",
              content.contains(String.format("width='%d'", testWidth)));
      assertTrue("Should have correct SVG height",
              content.contains(String.format("height='%d'", testHeight)));
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

  @Test
  public void testShapeRemoval() {
    model.addShape("R1", "rectangle");
    model.addShape("R2", "rectangle");
    model.takeSnapshot("Before removal");

    model.removeShape("R1");
    model.takeSnapshot("After removal");

    WebView view = new WebView(model, testOutputPath, 800, 600);
    view.display();

    try {
      List<String> lines = Files.readAllLines(Path.of(testOutputPath));
      String firstSnapshot = lines.stream()
              .dropWhile(line -> !line.contains("Before removal"))
              .takeWhile(line -> !line.contains("After removal"))
              .collect(Collectors.joining("\n"));

      String secondSnapshot = lines.stream()
              .dropWhile(line -> !line.contains("After removal"))
              .collect(Collectors.joining("\n"));

      assertEquals("First snapshot should have 2 rectangles",
              2, countOccurrences(firstSnapshot, "<rect"));
      assertEquals("Second snapshot should have 1 rectangle",
              1, countOccurrences(secondSnapshot, "<rect"));
    } catch (IOException e) {
      fail("Could not read test file: " + e.getMessage());
    }
  }

  private int countOccurrences(String str, String substr) {
    return (str.length() - str.replace(substr, "").length()) / substr.length();
  }

}