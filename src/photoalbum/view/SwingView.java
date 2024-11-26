package photoalbum.view;

import photoalbum.model.IPhotoAlbum;
import photoalbum.model.IShape;
import photoalbum.model.ISnapshot;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.Collections;

/**
 * A graphical view implementation for the photo album application using Swing. This class
 * provides a window-based interface for viewing and navigating through snapshots of shapes.
 */
public class SwingView extends JFrame implements IPhotoAlbumView {

  /**
   * The photo album model containing all shapes and snapshots to be displayed. This model
   * provides the data that will be rendered in the view.
   */
  private final IPhotoAlbum model;

  /**
   * The width of the drawing canvas in pixels. This determines how much horizontal space
   * is available for rendering shapes.
   */
  private final int width;

  /**
   * The height of the drawing canvas in pixels. This determines how much vertical space
   * is available for rendering shapes.
   */
  private final int height;

  /**
   * Tracks the currently displayed snapshot's index in the album's snapshot list. This
   * enables navigation between different snapshots.
   */
  private int currentSnapshotIndex = 0;

  /**
   * The panel responsible for rendering shapes from the current snapshot. This panel
   * handles all drawing operations.
   */
  private final DrawingPanel drawingPanel;

  /**
   * The panel responsible for rendering shapes from the current snapshot. This panel
   * handles all drawing operations.
   */
  private final JLabel snapshotInfo;

  /**
   * Creates a new SwingView with the specified model and dimensions.
   *
   * @param model The photo album model containing shapes and snapshots to display
   * @param width The width of the drawing canvas in pixels
   * @param height The height of the drawing canvas in pixels
   */

  public SwingView(IPhotoAlbum model, int width, int height) {
    this.model = model;
    this.width = width;
    this.height = height;

    setTitle("Photo Album");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

    drawingPanel = new DrawingPanel();
    snapshotInfo = new JLabel();
    snapshotInfo.setHorizontalAlignment(JLabel.CENTER);

    setupUI();
  }

  /**
   * Sets up the user interface components including navigation buttons and layout. This
   * method initializes all UI elements and arranges them in the window.
   */
  private void setupUI() {
    JPanel controlPanel = new JPanel();
    JButton prevButton = new JButton("Previous");
    JButton nextButton = new JButton("Next");

    prevButton.addActionListener(e -> navigateSnapshot(-1));
    nextButton.addActionListener(e -> navigateSnapshot(1));

    controlPanel.add(prevButton);
    controlPanel.add(nextButton);

    add(drawingPanel, BorderLayout.CENTER);
    add(controlPanel, BorderLayout.SOUTH);
    add(snapshotInfo, BorderLayout.NORTH);

    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Navigates to a different snapshot based on the specified direction.
   *
   * @param direction -1 for previous snapshot, 1 for next snapshot
   */
  private void navigateSnapshot(int direction) {
    int newIndex = currentSnapshotIndex + direction;
    if (newIndex >= 0 && newIndex < model.getSnapshots().size()) {
      currentSnapshotIndex = newIndex;
      updateView();
    }
  }

  /**
   * A custom panel that handles the rendering of shapes from the current snapshot. This
   * panel manages all drawing operations and shape layering.
   */
  private class DrawingPanel extends JPanel {

    /**
     * A pattern for extracting numeric values from shape coordinate strings. This
     * pattern matches both integer and decimal numbers.
     */
    private final Pattern numberPattern = Pattern.compile("[-+]?\\d*\\.?\\d+");

    /**
     * Renders the current snapshot's shapes in the correct layering order.
     *
     * @param g The graphics context used for drawing
     */
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      List<ISnapshot> snapshots = model.getSnapshots();
      if (!snapshots.isEmpty() && currentSnapshotIndex < snapshots.size()) {
        ISnapshot snapshot = snapshots.get(currentSnapshotIndex);

        // Sort shapes by rendering order
        List<IShape> sortedShapes = new ArrayList<>(snapshot.getShapes());
        Collections.sort(sortedShapes, (a, b) -> getRenderingPriority(a) - getRenderingPriority(b));

        // Draw all shapes in order
        for (IShape shape : sortedShapes) {
          drawShape(g2d, shape);
        }
      }
    }

    /**
     * Determines the rendering priority of a shape based on its type and name. Lower
     * numbers are rendered first, creating proper layering.
     *
     * @param shape The shape whose rendering priority is being determined
     * @return An integer priority value, where 0 is rendered first
     */
    private int getRenderingPriority(IShape shape) {
      String name = shape.getName().toLowerCase();

      // Background elements first
      if (name.contains("background") || name.contains("sky") ||
              name.contains("ground") || name.equals("rect1")) {
        return 0;
      }
      // Building/court elements second
      if (name.startsWith("b") || name.contains("court")) {
        return 1;
      }
      // Windows and details third
      if (name.startsWith("window")) {
        return 2;
      }
      // Special elements last (moon, decorations)
      if (name.contains("moon") || name.contains("ball")) {
        return 3;
      }
      // Default priority
      return 4;
    }

    /**
     * Draws a single shape using the provided graphics context.
     *
     * @param g2d The graphics context used for drawing
     * @param shape The shape to be drawn
     */
    private void drawShape(Graphics2D g2d, IShape shape) {
      try {
        // Set color
        String[] rgb = extractColor(shape);
        float r = Float.parseFloat(rgb[0].trim());
        float g = Float.parseFloat(rgb[1].trim());
        float b = Float.parseFloat(rgb[2].trim());
        g2d.setColor(new Color(r, g, b));

        // Get shape data
        List<Double> coords = extractShapeData(shape);
        if (coords.size() >= 4) {
          if (shape.getType().equals("rectangle")) {
            g2d.fillRect(
                    (int)coords.get(0).doubleValue(),  // x
                    (int)coords.get(1).doubleValue(),  // y
                    (int)coords.get(2).doubleValue(),  // width
                    (int)coords.get(3).doubleValue()   // height
            );
          } else if (shape.getType().equals("oval")) {
            double cx = coords.get(0);
            double cy = coords.get(1);
            double rx = coords.get(2);
            double ry = coords.get(3);
            g2d.fillOval(
                    (int)(cx - rx),
                    (int)(cy - ry),
                    (int)(rx * 2),
                    (int)(ry * 2)
            );
          }
        }
      } catch (Exception e) {
        System.err.println("Error drawing " + shape.getName() + ": " + e.getMessage());
      }
    }

    /**
     * Extracts RGB color values from a shape's string representation.
     *
     * @param shape The shape whose color is being extracted
     * @return An array of strings containing RGB color values
     */
    private String[] extractColor(IShape shape) {
      String str = shape.toString();
      int colorStart = str.lastIndexOf("Color: (") + 8;
      int colorEnd = str.lastIndexOf(")");
      return str.substring(colorStart, colorEnd).split(",");
    }

    /**
     * Extracts coordinate and dimension data from a shape's string representation.
     *
     * @param shape The shape whose data is being extracted
     * @return A list of doubles containing position and size information
     */
    private List<Double> extractShapeData(IShape shape) {
      List<Double> numbers = new ArrayList<>();
      String[] lines = shape.toString().split("\n");

      for (String line : lines) {
        if ((shape.getType().equals("rectangle") && line.contains("Min corner:")) ||
                (shape.getType().equals("oval") && line.contains("Center:"))) {
          Matcher matcher = numberPattern.matcher(line);
          while (matcher.find()) {
            numbers.add(Double.parseDouble(matcher.group()));
          }
          break;
        }
      }
      return numbers;
    }


    @Override
    public Dimension getPreferredSize() {
      return new Dimension(width, height);
    }
  }

  /**
   * Updates the view to reflect the current snapshot, refreshing both the info label
   * and the shape display.
   */
  private void updateView() {
    ISnapshot snapshot = model.getSnapshots().get(currentSnapshotIndex);
    snapshotInfo.setText(String.format("ID: %s | Description: %s",
            snapshot.getId(), snapshot.getDescription()));
    drawingPanel.repaint();
  }

  /**
   * Displays the view and shows the first snapshot if available.
   */
  @Override
  public void display() {
    setVisible(true);
    if (!model.getSnapshots().isEmpty()) {
      updateView();
    }
  }
}

