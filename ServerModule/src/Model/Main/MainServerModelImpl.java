package Model.Main;

import DataTypes.PlanningPoker;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainServerModelImpl implements MainServerModel, Runnable
{

  private PropertyChangeSupport support;
  private volatile static MainServerModel instance;
  private static final Lock lock = new ReentrantLock();
  private ArrayList<PlanningPoker> planningPokerGames;

  private MainServerModelImpl()
  {
    //TODO: Refactor so that it in the future loads the games from a database.
    support = new PropertyChangeSupport(this);
    planningPokerGames = new ArrayList<>();
  }

  @Override public boolean validatePlanningPoker(String planningPokerID)
  {
    for (PlanningPoker planningPoker : planningPokerGames)
    {
      if (planningPoker.getPlanningPokerID().equals(planningPokerID))
      {
        return true;
      }
    }
    return false;
  }

  @Override public PlanningPoker createPlanningPoker()
  {
    PlanningPoker planningPoker = new PlanningPoker();
    planningPoker.generatePlanningPokerID();
    System.out.println(
        "Creating planningPokerID: " + planningPoker.getPlanningPokerID());
    planningPokerGames.add(planningPoker);
    return planningPoker;
  }

  @Override public PlanningPoker getPlanningPokerGame(String planningPokerId)
  {
    if(validatePlanningPoker(planningPokerId)) {
      for (PlanningPoker planningPoker : planningPokerGames) {
        if(planningPoker.getPlanningPokerID().equals(planningPokerId)) {
          return planningPoker;
        }
      }
    }
    return null;
  }

  public static MainServerModel getInstance()
  {
    //Here we use the "Double-checked lock" principle to ensure proper synchronization.
    if (instance == null)
    {
      synchronized (lock)
      {
        if (instance == null)
        {
          instance = new MainServerModelImpl();
        }
      }
    }
    return instance;
  }

  @Override public void addPropertyChangeListener(
      PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(listener);
  }

  @Override public void addPropertyChangeListener(String name,
      PropertyChangeListener listener)
  {
    support.addPropertyChangeListener(name, listener);
  }

  @Override public void removePropertyChangeListener(
      PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(listener);
  }

  @Override public void removePropertyChangeListener(String name,
      PropertyChangeListener listener)
  {
    support.removePropertyChangeListener(name, listener);
  }

  @Override public void run()
  {
    //TODO
  }
}
