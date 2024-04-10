package Util;

import java.beans.PropertyChangeListener;
import java.io.Serializable;

public interface PropertyChangeSubject extends Serializable
{
  public void addPropertyChangeListener(PropertyChangeListener listener);
  public void addPropertyChangeListener(String name, PropertyChangeListener listener);
  public void removePropertyChangeListener(PropertyChangeListener listener);
  public void removePropertyChangeListener(String name, PropertyChangeListener listener);
}
