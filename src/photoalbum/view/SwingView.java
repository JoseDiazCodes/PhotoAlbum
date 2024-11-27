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

public class SwingView extends JFrame implements IPhotoAlbumView {
  private final IPhotoAlbum model;
  private final int width;
  private final int height;
  private int currentSnapshotIndex = 0;
  private final DrawingPanel drawingPanel;
  private final JLabel snapshotInfo;

  public SwingView(IPhotoAlbum model, int width, int height) {
    this.model = model;
    this.width = width;
    this.height = height;

    setTitle("Photo Album Viewer");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout(10, 10));

    drawingPanel = new DrawingPanel();
    snapshotInfo = new JLabel();
    snapshotInfo.setHorizontalAlignment(JLabel.CENTER);

    setupUI();
  }

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

  private JPanel createNavigationPanel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
    panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    JButton prevButton = new JButton("Previous");
    JButton nextButton = new JButton("Next");
    JButton selectButton = new JButton("Select Snapshot");

    prevButton.addActionListener(e -> showPreviousSnapshot());
    nextButton.addActionListener(e -> showNextSnapshot());
    selectButton.addActionListener(e -> showSnapshotSelector());

    panel.add(prevButton);
    panel.add(selectButton);
    panel.add(nextButton);

    return panel;
  }

  private void showPreviousSnapshot() {
    if (currentSnapshotIndex > 0) {
      currentSnapshotIndex--;
      updateView();
    } else {
      showMessage("You are at the first snapshot.");
    }
  }

  private void showNextSnapshot() {
    if (currentSnapshotIndex < model.getSnapshots().size() - 1) {
      currentSnapshotIndex++;
      updateView();
    } else {
      showMessage("You are at the last snapshot.");
    }
  }

  private void showSnapshotSelector() {
    List<ISnapshot> snapshots = model.getSnapshots();
    if (snapshots.isEmpty()) {
      showMessage("No snapshots available.");
      return;
    }

    String[] options = new String[snapshots.size()];
    for (int i = 0; i < snapshots.size(); i++) {
      ISnapshot snapshot = snapshots.get(i);
      String description = snapshot.getDescription().isEmpty() ?
              "Snapshot " + (i + 1) : snapshot.getDescription();
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

  private void showMessage(String message) {
    JOptionPane.showMessageDialog(
            this,
            message,
            "Navigation",
            JOptionPane.INFORMATION_MESSAGE
    );
  }

  private class DrawingPanel extends JPanel {
    private final Pattern numberPattern = Pattern.compile("[-+]?\\d*\\.?\\d+");

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

    private int getRenderingPriority(IShape shape) {
      String name = shape.getName().toLowerCase();

      if (name.contains("background") || name.contains("sky") ||
              name.contains("ground") || name.equals("rect1")) {
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

    private String[] extractColor(IShape shape) {
      String str = shape.toString();
      int colorStart = str.lastIndexOf("Color: (") + 8;
      int colorEnd = str.lastIndexOf(")");
      return str.substring(colorStart, colorEnd).split(",");
    }

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

  private void updateView() {
    ISnapshot snapshot = model.getSnapshots().get(currentSnapshotIndex);
    String description = snapshot.getDescription().isEmpty() ?
            "No description available" : snapshot.getDescription();
    snapshotInfo.setText(String.format("<html><center>Snapshot %d of %d<br>ID: %s<br>%s</center></html>",
            currentSnapshotIndex + 1,
            model.getSnapshots().size(),
            snapshot.getId(),
            description));
    drawingPanel.repaint();
  }

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