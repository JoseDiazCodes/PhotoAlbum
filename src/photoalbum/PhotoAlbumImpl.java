package photoalbum;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A concrete implementation of the IPhotoAlbum interface. This class manages a collection
 * of shapes and their snapshots, handling creation, modification, and persistence of the
 * album's state over time.
 */
public class PhotoAlbumImpl implements IPhotoAlbum {
  /**
   * Maps shape names to their instances for quick lookup and modification. The map
   * maintains all currently active shapes in the album.
   */
  private final Map<String, IShape> shapes;

  /**
   * An ordered list of all snapshots taken, maintaining their chronological sequence
   * from oldest to newest.
   */
  private final List<ISnapshot> snapshots;

  /**
   * Creates a new empty photo album with no shapes or snapshots.
   */
  public PhotoAlbumImpl() {
    this.shapes = new HashMap<>();
    this.snapshots = new ArrayList<>();
  }

  @Override
  public void addShape(String name, String type) {
    if (shapes.containsKey(name)) {
      throw new IllegalArgumentException("Shape name already exists: " + name);
    }

    IShape shape;
    if (type.toLowerCase().equals("rectangle")) {
      shape = new Rectangle(name, 0, 0, 50, 50);
    } else if (type.toLowerCase().equals("oval")) {
      shape = new Oval(name, 0, 0, 25, 25);
    } else {
      throw new IllegalArgumentException("Unsupported shape type: " + type);
    }

    shapes.put(name, shape);
  }

  @Override
  public void removeShape(String name) {
    if (!shapes.containsKey(name)) {
      throw new IllegalArgumentException("Shape not found: " + name);
    }
    shapes.remove(name);
  }

  @Override
  public IShape getShape(String name) {
    IShape shape = shapes.get(name);
    if (shape == null) {
      throw new IllegalArgumentException("Shape not found: " + name);
    }
    return shape;
  }

  @Override
  public String takeSnapshot(String description) {
    ISnapshot snapshot = new Snapshot(new ArrayList<>(shapes.values()), description);
    snapshots.add(snapshot);
    return snapshot.getId();
  }

  @Override
  public ISnapshot getSnapshot(String id) {
    for (ISnapshot snapshot : snapshots) {
      if (snapshot.getId().equals(id)) {
        return snapshot;
      }
    }
    throw new IllegalArgumentException("photoalbum.Snapshot not found: " + id);
  }

  @Override
  public List<ISnapshot> getSnapshots() {
    return Collections.unmodifiableList(snapshots);
  }

  @Override
  public String getSnapshotIDs() {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < snapshots.size(); i++) {
      sb.append(snapshots.get(i).getId());
      if (i < snapshots.size() - 1) {
        sb.append(", ");
      }
    }
    sb.append("]");
    return sb.toString();
  }

  @Override
  public String printSnapshots() {
    StringBuilder sb = new StringBuilder("Printing Snapshots\n");
    for (ISnapshot snapshot : snapshots) {
      sb.append(snapshot.toString()).append("\n\n");
    }
    return sb.toString();
  }

  @Override
  public void reset() {
    shapes.clear();
    snapshots.clear();
  }
}