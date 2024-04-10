package Model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class ServerModel_Impl implements ServerModel, Runnable
{
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);



  @Override public void run()
  {
    //TODO
  }


  @Override public void addPropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }
  @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener)
  {
    propertyChangeSupport.addPropertyChangeListener(name, listener);
  }
  @Override public void removePropertyChangeListener(PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener)
  {
    propertyChangeSupport.removePropertyChangeListener(name, listener);
  }
}
