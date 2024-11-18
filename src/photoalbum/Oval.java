package photoalbum;

/**
 * Represents an oval shape in the photo album defined by its center point and two radii.
 */
public class Oval extends AbstractShape {
  /**
   * The horizontal radius of this oval extending from its center point.
   */
  private double xRadius;

  /**
   * The vertical radius of this oval extending from its center point.
   */
  private double yRadius;

  /**
   * Creates a new oval with specified center position and radii.
   *
   * @param name Unique identifier for the oval
   * @param centerX X-coordinate of oval's center
   * @param centerY Y-coordinate of oval's center
   * @param xRadius Horizontal radius from center
   * @param yRadius Vertical radius from center
   */
  public Oval(String name, double centerX, double centerY, double xRadius, double yRadius) {
    super(name, centerX, centerY);
    this.xRadius = xRadius;
    this.yRadius = yRadius;
  }

  @Override
  public String getType() {
    return "oval";
  }

  @Override
  public void resize(double xRadius, double yRadius) {
    if (xRadius <= 0 || yRadius <= 0) {
      throw new IllegalArgumentException("Radii must be positive");
    }
    this.xRadius = xRadius;
    this.yRadius = yRadius;
  }

  @Override
  protected AbstractShape createCopy() {
    return new Oval(getName(), x, y, xRadius, yRadius);
  }

  @Override
  public String toString() {
    return String.format("Name: %s\nType: oval\n"
                    + "Center: (%.1f,%.1f), X radius: %.1f, Y radius: %.1f, Color: %s",
            getName(), x, y, xRadius, yRadius, getColorString());
  }
}