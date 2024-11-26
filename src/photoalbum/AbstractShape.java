package photoalbum;

import photoalbum.model.IShape;

/**
 * An abstract base class for 2D shapes that provides common shape functionality. This
 * class implements position, color, and identity behaviors shared by all shapes.
 */
public abstract class AbstractShape implements IShape {

  /**
   * The unique identifier for this shape that cannot be changed after creation.
   */
  private final String name;

  /**
   * The RGB color values for this shape stored as [red, green, blue], each 0.0-1.0.
   */
  private double[] color;

  /**
   * The x-coordinate of this shape's position on the canvas.
   */
  protected double x;

  /**
   * The y-coordinate of this shape's position on the canvas.
   */
  protected double y;

  /**
   * Creates a new shape with the given name and position, defaulting to red color.
   */
  protected AbstractShape(String name, double x, double y) {
    this.name = name;
    this.x = x;
    this.y = y;
    this.color = new double[]{1.0, 0.0, 0.0}; // Default red
  }

  /**
   * Gets this shape's unique identifier.
   */
  @Override
  public String getName() {
    return name;
  }

  /**
   * Sets shape's color using RGB values between 0.0 and 1.0.
   */
  @Override
  public void setColor(double r, double g, double b) {
    if (r < 0 || r > 1 || g < 0 || g > 1 || b < 0 || b > 1) {
      throw new IllegalArgumentException("Color components must be between 0 and 1");
    }
    this.color = new double[]{r, g, b};
  }

  /**
   * Moves this shape to a new position on the canvas.
   */
  @Override
  public void move(double x, double y) {
    this.x = x;
    this.y = y;
  }

  /**
   * Creates a formatted string of the current RGB color values.
   */
  protected String getColorString() {
    return String.format("(%.1f,%.1f,%.1f)", color[0], color[1], color[2]);
  }

  /**
   * Creates a new instance of this shape with the same dimensions.
   */
  protected abstract AbstractShape createCopy();

  /**
   * Creates a complete deep copy of this shape with all properties.
   */
  @Override
  public IShape copy() {
    AbstractShape copy = createCopy();
    copy.color = this.color.clone();
    return copy;
  }
}
