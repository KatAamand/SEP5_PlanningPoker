package Model.Lobby;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LobbyServerModelImpl implements LobbyServerModel {
  // TODO: This class is never used. Consider removing.

  private final PropertyChangeSupport propertyChangeSupport;
  private static volatile LobbyServerModel instance;
  private static final Lock lock = new ReentrantLock();


  private LobbyServerModelImpl()
  {
    propertyChangeSupport = new PropertyChangeSupport(this);
  }

  public static LobbyServerModel getInstance()
  {
    //Here we use the "Double-checked lock" principle to ensure proper synchronization.
    if(instance == null)
    {
      synchronized (lock)
      {
        if(instance == null)
        {
          instance = new LobbyServerModelImpl();
        }
      }
    }
    return instance;
  }

  @Override public void addPropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(listener);
  }
  @Override public void addPropertyChangeListener(String name, PropertyChangeListener listener) {
    propertyChangeSupport.addPropertyChangeListener(name, listener);
  }
  @Override public void removePropertyChangeListener(PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(listener);
  }
  @Override public void removePropertyChangeListener(String name, PropertyChangeListener listener) {
    propertyChangeSupport.removePropertyChangeListener(name, listener);
  }
}
