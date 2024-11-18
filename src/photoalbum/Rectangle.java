package photoalbum;

/**
 * Represents a rectangle shape in the photo album defined by its corner position,
 * width, and height.
 */
public class Rectangle extends AbstractShape {
  /**
   * The horizontal extent of this rectangle from its corner position.
   */
  private double width;

  /**
   * The vertical extent of this rectangle from its corner position.
   */
  private double height;

  /**
   * Creates a new rectangle with specified corner position and dimensions.
   *
   * @param name Unique identifier for the rectangle
   * @param x X-coordinate of top-left corner
   * @param y Y-coordinate of top-left corner
   * @param width Horizontal size
   * @param height Vertical size
   */
  public Rectangle(String name, double x, double y, double width, double height) {
    super(name, x, y);
    this.width = width;
    this.height = height;
  }

  @Override
  public String getType() {
    return "rectangle";
  }

  @Override
  public void resize(double width, double height) {
    if (width <= 0 || height <= 0) {
      throw new IllegalArgumentException("Dimensions must be positive");
    }
    this.width = width;
    this.height = height;
  }

  @Override
  protected AbstractShape createCopy() {
    return new Rectangle(getName(), x, y, width, height);
  }

  @Override
  public String toString() {
    return String.format("Name: %s\nType: rectangle\n" +
                    "Min corner: (%.1f,%.1f), Width: %.1f, Height: %.1f, Color: %s",
            getName(), x, y, width, height, getColorString());
  }
}