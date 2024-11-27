package photoalbum.view;

import photoalbum.model.IPhotoAlbum;
import photoalbum.model.IShape;
import photoalbum.model.ISnapshot;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

/**
 * Creates a window to view your photo album snapshots. Provides buttons to flip through
 * snapshots and see your shapes. This view updates in real-time as you click through
 * different snapshots.
 */
public class SwingView extends JFrame implements IPhotoAlbumView {
  /** The photo album containing all our shapes and snapshots. */
  private final IPhotoAlbum model;

  /** How wide our drawing area should be. */
  private final int width;

  /** How tall our drawing area should be. */
  private final int height;

  /** Which snapshot we're currently looking at. */
  private int currentSnapshotIndex = 0;

  /** The main area where we draw all our shapes. */
  private final DrawingPanel drawingPanel;

  /** Shows info about the current snapshot. */
  private final JLabel snapshotInfo;

  /**
   * Creates a new window to show snapshots from our photo album.
   *
   * @param model The photo album with all our shapes and snapshots
   * @param width How wide the window should be
   * @param height How tall the window should be
   */

  public SwingView(IPhotoAlbum model, int width, int height) {
    this.model = model;
    this.width = width;
    this.height = height;

    setTitle("Photo Album Viewer");
    setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));

    drawingPanel = new DrawingPanel();
    snapshotInfo = new JLabel();
    snapshotInfo.setHorizontalAlignment(JLabel.CENTER);

    setupUI();
  }

  /**
   * Sets up all the parts of our window - the navigation buttons, info panel, and
   * drawing area.
   */
  private void setupUI() {
    // Create info panel at top
    JPanel infoPanel = new JPanel(new BorderLayout());
    infoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    infoPanel.add(snapshotInfo, BorderLayout.CENTER);

    // Create navigation panel at bottom
    JPanel navigationPanel = createNavigationPanel();

    // Add panels to frame
    add(infoPanel, BorderLayout.NORTH);
    add(drawingPanel, BorderLayout.CENTER);
    add(navigationPanel, BorderLayout.SOUTH);

    pack();
    setLocationRelativeTo(null);
  }

  /**
   * Creates the panel with Previous/Next buttons and snapshot selector.
   *
   * @return A panel containing all navigation controls
   */
  private JPanel createNavigationPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    JButton prevButton = new JButton("Previous");
    JButton nextButton = new JButton("Next");
    JButton selectButton = new JButton("Select Snapshot");
    JButton quitButton = new JButton("Quit");

    prevButton.addActionListener(e -> showPreviousSnapshot());
    nextButton.addActionListener(e -> showNextSnapshot());
    selectButton.addActionListener(e -> showSnapshotSelector());
    quitButton.addActionListener(e -> handleQuit());

    panel.add(prevButton);
    panel.add(selectButton);
    panel.add(nextButton);
    panel.add(quitButton);

    return panel;
  }

  /**
   * Handles application exit with confirmation dialog.
   */
  private void handleQuit() {
    int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit?",
            "Exit Application",
            JOptionPane.YES_NO_OPTION
    );

    if (result == JOptionPane.YES_OPTION) {
      System.exit(0);
    }
  }

  /**
   * Goes back to the previous snapshot if there is one. Shows a message if we're
   * already at the first snapshot.
   */

  private void showPreviousSnapshot() {
    if (currentSnapshotIndex > 0) {
      currentSnapshotIndex--;
      updateView();
    } else {
      showMessage("You are at the first snapshot.");
    }
  }

  /**
   * Goes back to the previous snapshot if there is one. Shows a message if we're
   * already at the first snapshot.
   */

  private void showNextSnapshot() {
    if (currentSnapshotIndex < model.getSnapshots().size() - 1) {
      currentSnapshotIndex++;
      updateView();
    } else {
      showMessage("You are at the last snapshot.");
    }
  }

  /**
   * Goes back to the previous snapshot if there is one. Shows a message if we're
   * already at the first snapshot.
   */

  private void showSnapshotSelector() {
    List<ISnapshot> snapshots = model.getSnapshots();
    if (snapshots.isEmpty()) {
      showMessage("No snapshots available.");
      return;
    }

    String[] options = new String[snapshots.size()];
    for (int i = 0; i < snapshots.size(); i++) {
      ISnapshot snapshot = snapshots.get(i);
      String description = snapshot.getDescription().isEmpty()
              ? "Snapshot " + (i + 1) : snapshot.getDescription();
      options[i] = String.format("%d: %s", i + 1, description);
    }

    String selected = (String) JOptionPane.showInputDialog(
            this,
            "Choose a snapshot to view:",
            "Select Snapshot",
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[currentSnapshotIndex]
    );

    if (selected != null) {
      int selectedIndex = Integer.parseInt(selected.split(":")[0]) - 1;
      if (selectedIndex != currentSnapshotIndex) {
        currentSnapshotIndex = selectedIndex;
        updateView();
      }
    }
  }

  /**
   * Shows a popup message to the user.
   *
   * @param message The text to show in the popup
   */
  private void showMessage(String message) {
    JOptionPane.showMessageDialog(
            this,
            message,
            "Navigation",
            JOptionPane.INFORMATION_MESSAGE
    );
  }

  /**
   * The panel where we actually draw all our shapes. Handles the rendering of
   * rectangles, ovals, and any other shapes in our snapshots.
   */
  private class DrawingPanel extends JPanel {
    /** Pattern to find numbers in shape descriptions.
     * <a href="https://regex101.com/?ref=hackernoon.com">RegexHelper</a>
     * */
    private final Pattern numberPattern = Pattern.compile("[-+]?\\d*\\.?\\d+");

    /**
     * Draws the current snapshot's shapes in the right order. Background elements
     * are drawn first, followed by main shapes and details.
     */
    @Override
    protected void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

      List<ISnapshot> snapshots = model.getSnapshots();
      if (!snapshots.isEmpty() && currentSnapshotIndex < snapshots.size()) {
        ISnapshot snapshot = snapshots.get(currentSnapshotIndex);

        List<IShape> sortedShapes = new ArrayList<>(snapshot.getShapes());
        Collections.sort(sortedShapes, (a, b) -> getRenderingPriority(a) - getRenderingPriority(b));

        for (IShape shape : sortedShapes) {
          drawShape(g2d, shape);
        }
      }
    }

    /**
     * Figures out what order to draw shapes in. Lower numbers are drawn first,
     * appearing in the background.
     *
     * @param shape The shape we're checking
     * @return A priority number (0 = background, higher = foreground)
     */
    private int getRenderingPriority(IShape shape) {
      String name = shape.getName().toLowerCase();

      if (name.contains("background") || name.contains("sky")
              || name.contains("ground") || name.equals("rect1")) {
        return 0;
      }
      if (name.startsWith("b") || name.contains("court")) {
        return 1;
      }
      if (name.startsWith("window")) {
        return 2;
      }
      if (name.contains("moon") || name.contains("ball")) {
        return 3;
      }
      return 4;
    }

    /**
     * Draws a single shape on the panel.
     *
     * @param g2d The graphics context to draw with
     * @param shape The shape to draw
     */
    private void drawShape(Graphics2D g2d, IShape shape) {
      try {
        String[] rgb = extractColor(shape);
        float r = Float.parseFloat(rgb[0].trim());
        float g = Float.parseFloat(rgb[1].trim());
        float b = Float.parseFloat(rgb[2].trim());
        g2d.setColor(new Color(r, g, b));

        List<Double> coords = extractShapeData(shape);
        if (coords.size() >= 4) {
          if (shape.getType().equals("rectangle")) {
            g2d.fillRect(
                    (int)coords.get(0).doubleValue(),
                    (int)coords.get(1).doubleValue(),
                    (int)coords.get(2).doubleValue(),
                    (int)coords.get(3).doubleValue()
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
     * Gets the RGB color values from a shape's description.
     *
     * @param shape The shape to get colors from
     * @return Array of RGB values as strings
     */
    private String[] extractColor(IShape shape) {
      String str = shape.toString();
      int colorStart = str.lastIndexOf("Color: (") + 8;
      int colorEnd = str.lastIndexOf(")");
      return str.substring(colorStart, colorEnd).split(",");
    }

    /**
     * Gets position and size information from a shape's description.
     *
     * @param shape The shape to get data from
     * @return List of numbers for position and dimensions
     */
    private List<Double> extractShapeData(IShape shape) {
      List<Double> numbers = new ArrayList<>();
      String[] lines = shape.toString().split("\n");

      for (String line : lines) {
        if ((shape.getType().equals("rectangle") && line.contains("Min corner:"))
                || (shape.getType().equals("oval") && line.contains("Center:"))) {
          Matcher matcher = numberPattern.matcher(line);
          while (matcher.find()) {
            numbers.add(Double.parseDouble(matcher.group()));
          }
          break;
        }
      }
      return numbers;
    }



    /**
     * Gets/returns preferred size.
     */
    @Override
    public Dimension getPreferredSize() {
      return new Dimension(width, height);
    }
  }

  /**
   * Updates what's shown in the window for the current snapshot. Updates both the
   * info panel and redraws all shapes.
   */
  private void updateView() {
    ISnapshot snapshot = model.getSnapshots().get(currentSnapshotIndex);
    String description = snapshot.getDescription().isEmpty()
            ? "No description available" : snapshot.getDescription();
    snapshotInfo.setText(
            String.format("<html><center>Snapshot %d of %d<br>ID: %s<br>%s</center></html>",
            currentSnapshotIndex + 1,
            model.getSnapshots().size(),
            snapshot.getId(),
            description));
    drawingPanel.repaint();
  }


  /**
   * Shows the window and displays the first snapshot if available.
   */
  @Override
  public void display() {
    setVisible(true);
    if (!model.getSnapshots().isEmpty()) {
      updateView();
    } else {
      showMessage("No snapshots available to display.");
    }
  }
}