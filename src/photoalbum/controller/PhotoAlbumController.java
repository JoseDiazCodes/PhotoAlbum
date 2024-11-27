package photoalbum.controller;

import photoalbum.model.IPhotoAlbum;
import photoalbum.view.IPhotoAlbumView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * Handles the photo album's main operations. Reads commands from a file and tells the
 * model and view what to do.
 */
public class PhotoAlbumController implements IPhotoAlbumController {

  /**
   * model of type photoalbum.
   * */
  private final IPhotoAlbum model;

  /**
   * view of type photoalbum.
   * */
  private final IPhotoAlbumView view;

  /**
   * Creates a new controller to manage the photo album's operations.
   *
   * @param model The photo album that stores shapes and snapshots
   * @param view How we'll show the album (either web page or window)
   */
  public PhotoAlbumController(IPhotoAlbum model, IPhotoAlbumView view) {
    this.model = model;
    this.view = view;
  }

  /**
   * Reads commands from a file and processes them one by one. Each line should have
   * a command like "shape", "move", or "snapshot" followed by its parameters.
   *
   * @param inputFile Path to the file containing commands
   */
  @Override
  public void processInput(String inputFile) {
    try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
      String line;
      while ((line = reader.readLine()) != null) {
        processCommand(line.trim());
      }
    } catch (IOException e) {
      throw new RuntimeException("Error reading input file: " + e.getMessage());
    }
  }

  /**
   * Takes a single command line and executes it. Skips empty lines and comments
   * (lines starting with #).
   *
   * @param line A single line from the input file
   */
  private void processCommand(String line) {
    if (line.isEmpty() || line.startsWith("#")) {
      return;
    }

    Scanner scanner = new Scanner(line);
    String command = scanner.next().toLowerCase();

    switch (command) {
      case "shape":
        processShapeCommand(scanner);
        break;
      case "move":
        processMoveCommand(scanner);
        break;
      case "color":
        processColorCommand(scanner);
        break;
      case "resize":
        processResizeCommand(scanner);
        break;
      case "remove":
        processRemoveCommand(scanner);
        break;
      case "snapshot":
        processSnapshotCommand(scanner);
        break;
    }
  }

  /**
   * Creates a new shape with the given properties and adds it to the album.
   * Expected format: name type x y width height red green blue
   */
  private void processShapeCommand(Scanner scanner) {
    String id = scanner.next();
    String type = scanner.next();
    double x = scanner.nextDouble();
    double y = scanner.nextDouble();
    double w = scanner.nextDouble();
    double h = scanner.nextDouble();
    int r = scanner.nextInt();
    int g = scanner.nextInt();
    int b = scanner.nextInt();

    model.addShape(id, type);
    model.getShape(id).setColor(r / 255.0, g / 255.0, b / 255.0);
    model.getShape(id).resize(w, h);
    model.getShape(id).move(x, y);

  }

  /**
   * Moves an existing shape to a new position.
   * Expected format: name new-x new-y
   */
  private void processMoveCommand(Scanner scanner) {
    String id = scanner.next();
    double x = scanner.nextDouble();
    double y = scanner.nextDouble();
    model.getShape(id).move(x, y);
  }

  /**
   * Changes a shape's color to new RGB values.
   * Expected format: name red green blue (0-255 for each color)
   */
  private void processColorCommand(Scanner scanner) {
    String id = scanner.next();
    int r = scanner.nextInt();
    int g = scanner.nextInt();
    int b = scanner.nextInt();
    model.getShape(id).setColor(r / 255.0, g / 255.0, b / 255.0);
  }

  /**
   * Changes a shape's size.
   * Expected format: name new-width new-height
   */

  private void processResizeCommand(Scanner scanner) {
    String id = scanner.next();
    double w = scanner.nextDouble();
    double h = scanner.nextDouble();
    model.getShape(id).resize(w, h);
  }

  /**
   * Removes a shape from the album.
   * Expected format: name
   */
  private void processRemoveCommand(Scanner scanner) {
    String id = scanner.next();
    model.removeShape(id);
  }

  /**
   * Takes a snapshot of the current state with an optional description.
   * The description is everything after "snapshot" on the line.
   */
  private void processSnapshotCommand(Scanner scanner) {
    String description = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    model.takeSnapshot(description);
  }

  /**
   * Shows the album using whatever view was provided (web page or window).
   */
  @Override
  public void start() {
    view.display();
  }
}