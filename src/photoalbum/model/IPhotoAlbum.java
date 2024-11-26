package photoalbum.model;

import java.util.List;

/**
 * Controls a photo album of 2D shapes. This interface manages the complete lifecycle of
 * shapes and snapshots, allowing shapes to be added, modified, removed, and captured in
 * snapshots. The album maintains a sequential history of snapshots that can be retrieved
 * and displayed in various formats.
 */
public interface IPhotoAlbum {
  /**
   * Creates and adds a new shape to the album. The shape type must be either "rectangle"
   * or "oval". Each shape must have a unique name within the album.
   *
   * @param name Unique identifier for the new shape
   * @param type Must be either "rectangle" or "oval"
   * @throws IllegalArgumentException if name is already taken or type is invalid
   */
  void addShape(String name, String type);

  /**
   * Removes a shape from the album. Once removed, the shape no longer appears in new
   * snapshots but remains in any existing snapshots taken before removal.
   *
   * @param name The identifier of the shape to remove
   * @throws IllegalArgumentException if no shape exists with the given name
   */
  void removeShape(String name);

  /**
   * Retrieves a shape from the album for modification. Changes made to the returned
   * shape are reflected on the album and future snapshots.
   *
   * @param name The identifier of the shape to retrieve
   * @return The shape object that can be modified
   * @throws IllegalArgumentException if no shape exists with the given name
   */
  IShape getShape(String name);

  /**
   * Creates a snapshot preserving the current state of all shapes in the album. The
   * snapshot maintains an independent copy of each shape's state at this moment.
   *
   * @param description Optional text describing this snapshot's contents
   * @return A unique identifier for retrieving this snapshot later
   */
  String takeSnapshot(String description);

  /**
   * Retrieves a previously taken snapshot by its identifier. The returned snapshot
   * reflects the exact state of all shapes when the snapshot was taken.
   *
   * @param id The unique identifier from takeSnapshot()
   * @return The snapshot object containing shape states
   * @throws IllegalArgumentException if no snapshot exists with the given id
   */
  ISnapshot getSnapshot(String id);

  /**
   * Returns all snapshots taken, ordered from oldest to newest. Modifications to the
   * returned list do not affect the album's snapshots.
   *
   * @return An unmodifiable list of all snapshots
   */
  List<ISnapshot> getSnapshots();

  /**
   * Returns a formatted string containing the IDs of all snapshots taken. Format is:
   * [id1, id2, id3, ...].
   *
   * @return A string listing all snapshot IDs
   */
  String getSnapshotIDs();

  /**
   * Creates a detailed text representation of all snapshots and their contents,
   * formatted according to assignment specifications.
   *
   * @return A formatted string showing all snapshots and their shapes
   */
  String printSnapshots();

  /**
   * Removes all shapes and snapshots from the album, returning it to its initial empty
   * state. This operation cannot be undone.
   */
  void reset();
}