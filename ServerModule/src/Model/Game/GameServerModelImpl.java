package Model.Game;

import DataTypes.Effort;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class GameServerModelImpl implements GameServerModel, Runnable{

  private PropertyChangeSupport propertyChangeSupport;
  private static GameServerModel instance;
  private static final Lock lock = new ReentrantLock();
  private ArrayList<Effort> efforts;


  private GameServerModelImpl()
  {
    this.efforts = new ArrayList<>();
    populateEfforts();
  }

  private void populateEfforts() {
    String[] effortNames = {"0", "½", "1", "2", "3", "5", "8", "13", "20", "40", "100", "?", "∞"};
    String pathForImg = "images/";
    String[] effortImgTitle = {"0.png", "half.png", "1.png", "2.png", "3.png", "5.png", "8.png", "13.png", "20.png", "40.png", "100.png", "question.png", "infinity.png"};

    for (int i = 0; i < effortNames.length; i++) {
      Effort effort = new Effort(effortNames[i], pathForImg + effortImgTitle[i]);
      efforts.add(effort);
    }
  }

  public ArrayList<Effort> getEfforts() {
    return efforts;
  }

  public static GameServerModel getInstance()
  {
    //Here we use the "Double-checked lock" principle to ensure proper synchronization.
    if(instance == null)
    {
      synchronized (lock)
      {
        if(instance == null)
        {
          instance = new GameServerModelImpl();
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

  @Override public void run()
  {
    //TODO
  }
}
