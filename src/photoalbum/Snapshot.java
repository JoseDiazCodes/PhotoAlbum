package photoalbum;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A concrete implementation of ISnapshot that captures and stores the state of all shapes
 * at a specific moment in time. Ensures shape states remain immutable once captured.
 */
public class Snapshot implements ISnapshot {
  /**
   * The unique identifier for this snapshot, generated from timestamp of creation.
   */
  private final String id;

  /**
   * The formatted timestamp showing when this snapshot was taken.
   */
  private final String timestamp;

  /**
   * An optional description explaining what state this snapshot captures.
   */
  private final String description;

  /**
   * An immutable list of shapes copied at the moment this snapshot was taken.
   */
  private final List<IShape> shapes;

  /**
   * Creates a new snapshot of the given shapes with an optional description.
   *
   * @param shapes List of shapes to capture
   * @param description Optional text describing this snapshot
   */
  public Snapshot(List<IShape> shapes, String description) {
    LocalDateTime now = LocalDateTime.now();
    this.id = now.toString();
    this.timestamp = now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss"));
    this.description = description;
    this.shapes = shapes.stream()
            .map(IShape::copy)
            .collect(Collectors.toUnmodifiableList());
  }

  @Override
  public String getId() { return id; }

  @Override
  public String getTimestamp() { return timestamp; }

  @Override
  public String getDescription() { return description; }

  @Override
  public List<IShape> getShapes() { return shapes; }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder()
            .append("photoalbum.Snapshot ID: ").append(id).append("\n")
            .append("Timestamp: ").append(timestamp).append("\n")
            .append("Description: ").append(description).append("\n")
            .append("Shape Information:\n");

    for (IShape shape : shapes) {
      sb.append(shape.toString()).append("\n\n");
    }
    return sb.toString().trim();
  }
}