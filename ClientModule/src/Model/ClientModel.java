package Model;

import Util.PropertyChangeSubject;

/** Client-side interface that bridges the UI related elements with the data related elements. */
public interface ClientModel extends PropertyChangeSubject
{
  /** Primary initialization method. Should be initialized shortly after any constructor, or run inside a
   * Platform.runLater() method to ensure increase thread safety with javaFx.*/
  void init();


  //TODO: Add more UI related methods!


    void sendMessage(String message);
}
