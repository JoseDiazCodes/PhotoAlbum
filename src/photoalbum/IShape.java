package photoalbum;

/**
 * Defines the core functionality of a shape in the photo album. Shapes are identified by
 * name and type, can be moved around the canvas, change color, resize, and create copies
 * of themselves. Each implementation must maintain its own specific dimension properties
 * while adhering to this common interface.
 */
public interface IShape {
  /**
   * Returns the unique identifier assigned to this shape when it was created. This name
   * remains constant throughout the shape's lifetime.
   *
   * @return The shape's unique name within the album
   */
  String getName();

  /**
   * Returns the type of this shape (e.g., "rectangle" or "oval"). This allows the
   * system to identify different shape categories without using instanceof.
   *
   * @return A string identifying the shape type
   */
  String getType();

  /**
   * Moves this shape to a new position on the canvas. The interpretation of x,y depends
   * on the specific shape type (e.g., corner vs center point).
   *
   * @param x The new x-coordinate
   * @param y The new y-coordinate
   */
  void move(double x, double y);

  /**
   * Changes this shape's color using RGB values. Each component must be between 0.0 and
   * 1.0 inclusive, where 0.0 is none of that color and 1.0 is full intensity.
   *
   * @param r Red component (0.0-1.0)
   * @param g Green component (0.0-1.0)
   * @param b Blue component (0.0-1.0)
   * @throws IllegalArgumentException if any color component is outside [0,1]
   */
  void setColor(double r, double g, double b);

  /**
   * Resizes this shape according to its type-specific parameters. For rectangles, these
   * are width and height. For ovals, these are x-radius and y-radius.
   *
   * @param param1 First dimension parameter
   * @param param2 Second dimension parameter
   * @throws IllegalArgumentException if any parameter is invalid (e.g., negative)
   */
  void resize(double param1, double param2);

  /**
   * Creates an independent copy of this shape with identical properties. The copy can
   * be modified without affecting the original shape.
   *
   * @return A new shape instance with the same properties
   */
  IShape copy();

  /**
   * Creates a string representation of this shape's current state according to the
   * assignment specification format.
   *
   * @return A formatted string describing the shape
   */
  String toString();
}