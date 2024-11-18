package photoalbum;

import java.util.List;

/**
 * Represents a moment-in-time capture of all shapes in the photo album. A snapshot
 * preserves the exact state of every shape present when it was taken, including their
 * positions, colors, and dimensions. Snapshots are immutable and maintain state
 * independently of the ongoing changes on the album.
 */
public interface ISnapshot {
  /**
   * Returns this snapshot's unique identifier, typically a timestamp of when it was
   * taken. This ID is used to retrieve the snapshot from the album later.
   *
   * @return The unique identifier string
   */
  String getId();

  /**
   * Returns a formatted string of when this snapshot was taken, using the pattern
   * "dd-MM-yyyy HH:mm:ss" as specified in requirements.
   *
   * @return The formatted timestamp string
   */
  String getTimestamp();

  /**
   * Returns the optional description provided when this snapshot was taken. This helps
   * users understand what state or changes the snapshot is capturing.
   *
   * @return The snapshot description, or empty string if none was provided
   */
  String getDescription();

  /**
   * Returns all shapes that were present when this snapshot was taken. The returned
   * shapes are copies, ensuring snapshot immutability.
   *
   * @return An unmodifiable list of shapes in their preserved state
   */
  List<IShape> getShapes();

  /**
   * Creates a formatted string representation of this snapshot including ID, timestamp,
   * description, and complete details of all shapes according to specification.
   *
   * @return A string containing all snapshot information
   */
  String toString();
}