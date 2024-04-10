package Model;

import DataTypes.User;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

public class ServerModel_Impl implements ServerModel, Runnable
{
  private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
  private ArrayList<Session> sessions;
  private ArrayList<User> users;



  public ServerModel_Impl()
  {
    //TODO
  }



  public void addSession(Session session)
  {
    this.sessions.add(session);
  }

  public boolean removeSession(Session session)
  {
    return this.sessions.remove(session);
  }

  public Session getSession(int index) throws NullPointerException
  {
    return this.sessions.get(index);
  }

  public Session getSession(String connectionCode) throws NullPointerException
  {
    for (int i = 0; i < sessions.size(); i++)
    {
      if(sessions.get(i).getConnectionCode())
      {
        return sessions.get(i);
      }
    }
    throw new NullPointerException();
    return null;
  }






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
