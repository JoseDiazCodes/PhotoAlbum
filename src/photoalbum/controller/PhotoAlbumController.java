package photoalbum.controller;

import photoalbum.model.IPhotoAlbum;
import photoalbum.view.IPhotoAlbumView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class PhotoAlbumController implements IPhotoAlbumController {
  private final IPhotoAlbum model;
  private final IPhotoAlbumView view;

  public PhotoAlbumController(IPhotoAlbum model, IPhotoAlbumView view) {
    this.model = model;
    this.view = view;
  }

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
    model.getShape(id).move(x, y);
    model.getShape(id).resize(w, h);
    model.getShape(id).setColor(r/255.0, g/255.0, b/255.0);
  }

  private void processMoveCommand(Scanner scanner) {
    String id = scanner.next();
    double x = scanner.nextDouble();
    double y = scanner.nextDouble();
    model.getShape(id).move(x, y);
  }

  private void processColorCommand(Scanner scanner) {
    String id = scanner.next();
    int r = scanner.nextInt();
    int g = scanner.nextInt();
    int b = scanner.nextInt();
    model.getShape(id).setColor(r/255.0, g/255.0, b/255.0);
  }

  private void processResizeCommand(Scanner scanner) {
    String id = scanner.next();
    double w = scanner.nextDouble();
    double h = scanner.nextDouble();
    model.getShape(id).resize(w, h);
  }

  private void processRemoveCommand(Scanner scanner) {
    String id = scanner.next();
    model.removeShape(id);
  }

  private void processSnapshotCommand(Scanner scanner) {
    String description = scanner.hasNextLine() ? scanner.nextLine().trim() : "";
    model.takeSnapshot(description);
  }

  @Override
  public void start() {
    view.display();
  }
}