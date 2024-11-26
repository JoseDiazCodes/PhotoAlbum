package photoalbum;

import photoalbum.model.IPhotoAlbum;
import photoalbum.model.PhotoAlbumImpl;
import photoalbum.view.IPhotoAlbumView;
import photoalbum.view.SwingView;
import photoalbum.view.WebView;
import photoalbum.controller.IPhotoAlbumController;
import photoalbum.controller.PhotoAlbumController;

public class PhotoAlbumMain {
  public static void main(String[] args) {
    int width = 1000;
    int height = 1000;
    String inputFile = null;
    String outputFile = null;
    String viewType = null;

    // Parse command line arguments
    for (int i = 0; i < args.length; i++) {
      switch (args[i].toLowerCase()) {
        case "-in":
          inputFile = args[++i];
          break;
        case "-out":
          outputFile = args[++i];
          break;
        case "-view":
        case "-v":
          viewType = args[++i];
          break;
        default:
          // Check if these are the optional width/height parameters
          try {
            width = Integer.parseInt(args[i]);
            height = Integer.parseInt(args[++i]);
          } catch (NumberFormatException e) {
            System.err.println("Invalid argument: " + args[i]);
            System.exit(1);
          }
      }
    }

    // Validate required arguments
    if (inputFile == null || viewType == null) {
      System.err.println("" +
              "Missing required arguments. Usage: -in <file> -view <type> "
              + "[-out <file>] [width height]");
      System.exit(1);
    }

    // Validate web view requires output file
    if (viewType.equalsIgnoreCase("web") && outputFile == null) {
      System.err.println("Web view requires output file specification (-out parameter)");
      System.exit(1);
    }

    // Create model and controller
    IPhotoAlbum model = new PhotoAlbumImpl();
    IPhotoAlbumView view;

    // Create appropriate view
    if (viewType.equalsIgnoreCase("graphical")) {
      view = new SwingView(model, width, height);
    } else if (viewType.equalsIgnoreCase("web")) {
      view = new WebView(model, outputFile, width, height);
    } else {
      System.err.println("Invalid view type: " + viewType);
      System.exit(1);
      return;
    }

    // Create and start controller
    IPhotoAlbumController controller = new PhotoAlbumController(model, view);
    controller.processInput(inputFile);
    controller.start();
  }
}