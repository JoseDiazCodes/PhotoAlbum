package photoalbum;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;


/**
 * A complete test suite for the photo album implementation that verifies core
 * functionality and behavior matches requirements.
 */
public class PhotoAlbumTests {
  /**
   * A photo album instance that is reset before each test to ensure a clean state.
   */
  private IPhotoAlbum album;

  /**
   * Creates a new photo album before each test is run.
   */
  @Before
  public void setup() {
    album = new PhotoAlbumImpl();
  }

  /**
   * Verifies that basic shape creation works for both rectangles and ovals.
   */

  @Test
  public void testCreateShapes() {
    album.addShape("R", "rectangle");
    IShape rectangle = album.getShape("R");
    Assert.assertEquals("rectangle", rectangle.getType());

    album.addShape("O", "oval");
    IShape oval = album.getShape("O");
    Assert.assertEquals("oval", oval.getType());
  }

  /**
   * Tests that shapes can be moved to specific coordinates.
   */
  @Test
  public void testMoveShape() {
    album.addShape("R", "rectangle");
    IShape shape = album.getShape("R");
    shape.move(200, 200);
    String output = shape.toString();
    assertTrue(output.contains("Min corner: (200.0,200.0)"));
  }

  /**
   * Verifies that shape colors can be modified and appear correctly in output.
   */
  @Test
  public void testChangeColor() {
    album.addShape("O", "oval");
    IShape oval = album.getShape("O");
    oval.setColor(0.0, 0.0, 1.0);
    String output = oval.toString();
    assertTrue(output.contains("Color: (0.0,0.0,1.0)"));
  }

  /**
   * Tests that snapshots properly capture the current state of all shapes.
   */
  @Test
  public void testTakeSnapshot() {
    album.addShape("R", "rectangle");
    album.addShape("O", "oval");
    album.getShape("R").move(200, 200);
    album.getShape("O").setColor(0.0, 0.0, 1.0);

    String id = album.takeSnapshot("First snapshot");
    ISnapshot snapshot = album.getSnapshot(id);

    Assert.assertEquals("First snapshot", snapshot.getDescription());
    Assert.assertEquals(2, snapshot.getShapes().size());
  }

  /**
   * Verifies that shapes can be removed and snapshots reflect the removal.
   */
  @Test
  public void testRemoveShape() {
    album.addShape("R", "rectangle");
    album.addShape("O", "oval");
    String id = album.takeSnapshot("Before removal");

    album.removeShape("R");
    String id2 = album.takeSnapshot("After removal");

    Assert.assertEquals(2, album.getSnapshot(id).getShapes().size());
    Assert.assertEquals(1, album.getSnapshot(id2).getShapes().size());
  }

  /**
   * Tests that reset clears all shapes and snapshots from the album.
   */
  @Test
  public void testReset() {
    album.addShape("R", "rectangle");
    album.takeSnapshot("Before reset");
    album.reset();

    try {
      album.getShape("R");
      fail("Expected exception after reset");
    } catch (IllegalArgumentException e) {
      // Expected
    }

    Assert.assertEquals("[]", album.getSnapshotIDs());
  }

  /**
   * Verifies that snapshots maintain independent copies of shape states.
   */
  @Test
  public void testShapeStateIndependence() {
    album.addShape("R", "rectangle");
    album.getShape("R").move(200, 200);
    String id1 = album.takeSnapshot("Position 1");

    album.getShape("R").move(300, 300);
    String id2 = album.takeSnapshot("Position 2");

    ISnapshot snapshot1 = album.getSnapshot(id1);
    ISnapshot snapshot2 = album.getSnapshot(id2);

    String pos1 = snapshot1.getShapes().get(0).toString();
    String pos2 = snapshot2.getShapes().get(0).toString();

    assertTrue(pos1.contains("(200.0,200.0)"));
    assertTrue(pos2.contains("(300.0,300.0)"));
  }

  /**
   * Tests that duplicate shape names are rejected as invalid.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testDuplicateShapeName() {
    album.addShape("R", "rectangle");
    album.addShape("R", "oval");
  }

  /**
   * Verifies that invalid shape types are rejected.
   */
  @Test(expected = IllegalArgumentException.class)
  public void testInvalidShapeType() {
    album.addShape("T", "triangle");
  }

  /**
   * Tests that snapshot output exactly matches format specified in requirements.
   */
  @Test
  public void testSnapshotOutput() {
    album.addShape("R", "rectangle");
    IShape rect = album.getShape("R");
    rect.move(200, 200);
    rect.resize(50, 100);
    rect.setColor(1.0, 0.0, 0.0);

    album.addShape("O", "oval");
    IShape oval = album.getShape("O");
    oval.move(500, 100);
    oval.resize(60, 30);
    oval.setColor(0.0, 0.0, 1.0);

    String id = album.takeSnapshot("After first selfie");
    String output = album.getSnapshot(id).toString();

    assertTrue(output.contains("Description: After first selfie"));
    assertTrue(output.contains("Min corner: (200.0,200.0), Width: 50.0, Height: 100.0, Color: (1.0,0.0,0.0)"));
    assertTrue(output.contains("Center: (500.0,100.0), X radius: 60.0, Y radius: 30.0, Color: (0.0,0.0,1.0)"));
  }

  /**
   * Tests a complete workflow scenario from shape creation through multiple transformations.
   */
  @Test
  public void testCompleteScenario() {
    // Create initial shapes
    album.addShape("R", "rectangle");
    album.addShape("O", "oval");
    IShape rect = album.getShape("R");
    IShape oval = album.getShape("O");

    // Set initial properties
    rect.move(200, 200);
    rect.resize(50, 100);
    rect.setColor(1.0, 0.0, 0.0);
    oval.move(500, 100);
    oval.resize(60, 30);
    oval.setColor(0.0, 0.0, 1.0);
    String id1 = album.takeSnapshot("Initial state");

    // Move shapes
    rect.move(300, 300);
    oval.move(500, 400);
    String id2 = album.takeSnapshot("After moving");

    // Change colors
    oval.setColor(0.0, 1.0, 0.0);
    String id3 = album.takeSnapshot("After color change");

    // Modify rectangle
    rect.resize(25, 100);
    String id4 = album.takeSnapshot("After resize");

    String allSnapshots = album.printSnapshots();
    assertTrue(allSnapshots.contains("Initial state"));
    assertTrue(allSnapshots.contains("After moving"));
    assertTrue(allSnapshots.contains("After color change"));
    assertTrue(allSnapshots.contains("After resize"));
  }

  /**
   * Verifies that shape dimensions are properly captured and displayed in snapshots.
   */
  @Test
  public void testShapeDimensions() {
    album.addShape("R", "rectangle");
    IShape rect = album.getShape("R");
    rect.resize(50, 100);

    album.addShape("O", "oval");
    IShape oval = album.getShape("O");
    oval.resize(60, 30);

    ISnapshot snapshot = album.getSnapshot(album.takeSnapshot("Test description"));
    String output = snapshot.toString();

    System.out.println("Full snapshot output:");
    System.out.println(output);

    // Test each section separately
    assertTrue(output.contains("photoalbum.Snapshot ID:"));
    assertTrue(output.contains("Timestamp:"));
    assertTrue(output.contains("Description: Test description"));
    assertTrue(output.contains("Shape Information:"));
    assertTrue(output.contains("Type: rectangle"));
    assertTrue(output.contains("Type: oval"));
    assertTrue(output.contains("Width: 50.0, Height: 100.0"));
    assertTrue(output.contains("X radius: 60.0, Y radius: 30.0"));
  }

  /**
   * Tests that color values are properly validated to be between 0 and 1.
   */
  @Test
  public void testColorValidation() {
    album.addShape("R", "rectangle");
    IShape shape = album.getShape("R");

    try {
      shape.setColor(-0.1, 0.5, 0.5);
      fail("Should not accept negative color values");
    } catch (IllegalArgumentException e) {}

    try {
      shape.setColor(0.5, 1.1, 0.5);
      fail("Should not accept color values > 1.0");
    } catch (IllegalArgumentException e) {}
  }

  /**
   * Verifies that reset properly clears both shapes and snapshots from the album.
   */
  @Test
  public void testSnapshotPersistence() {
    album.addShape("R", "rectangle");
    String id = album.takeSnapshot("First");
    album.reset();

    try {
      album.getShape("R");
      fail("Shape should not exist after reset");
    } catch (IllegalArgumentException e) {}

    try {
      album.getSnapshot(id);
      fail("photoalbum.Snapshot should not exist after reset");
    } catch (IllegalArgumentException e) {}
  }
}
