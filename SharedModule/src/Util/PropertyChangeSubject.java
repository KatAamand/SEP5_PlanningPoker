package Util;

import java.beans.PropertyChangeListener;
import java.io.Serializable;


/** This interface is used to define the subject in an Obervable relationship. It extends Serializable.
 * It allows Observers to attach listeners to the subject in order to Observe for specific events. */
public interface PropertyChangeSubject extends Serializable
{
  /**<p>Assigns a listener to this subject<br></p>
   * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a PropertyChangeEvent is fired).
   */
  void addPropertyChangeListener(PropertyChangeListener listener);


  /**<p>Assigns a listener to this subject<br></p>
   * @param name A String reference to the event name a specific PropertyChangeEvent.
   * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired).
   */
  void addPropertyChangeListener(String name, PropertyChangeListener listener);


  /**<p>Removes a listener from this subject<br></p>
   * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired).
   */
  void removePropertyChangeListener(PropertyChangeListener listener);


  /**<p>Removes a listener from this subject<br></p>
   * @param name A String reference to the event name a specific PropertyChangeEvent.
   * @param listener A PropertyChangeListener, i.e. some code or methods that should be executed when a named specific PropertyChangeEvent is fired).
   */
  void removePropertyChangeListener(String name, PropertyChangeListener listener);
}