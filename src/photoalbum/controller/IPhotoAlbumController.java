package photoalbum.controller;

/**
 * IPhotoAlbum controller which is used in MVC.
 */
public interface IPhotoAlbumController {
  /**
   * Process commands from the input file.
   */
  void processInput(String inputFile);

  /**
   * Start the view.
   */
  void start();
}