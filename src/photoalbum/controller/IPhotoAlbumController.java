package photoalbum.controller;

public interface IPhotoAlbumController {
  /**
   * Process commands from the input file
   */
  void processInput(String inputFile);

  /**
   * Start the view
   */
  void start();
}