package Views;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** This class is a part of the test-suite, and is implemented as a Singleton with a basic lock which is used in the test scenarios which require testing of UI related elements - such as the views.
 * This simply allows for test classes to acquire a centralized lock, forcing test classes to be run 1 at a time - instead of concurrently. */
public class ForceSynchronizationOfScenarioTestClasses
{
  private static final Lock localLock = new ReentrantLock();
  private static final Lock synchronizationLock = new ReentrantLock();
  private volatile static ForceSynchronizationOfScenarioTestClasses instance;


  public static Lock getSynchronizationLock() {
    //Here we use the "Double-checked lock" principle to ensure proper synchronization.
    if(instance == null) {
      synchronized (localLock) {
        if(instance == null) {
          instance = new ForceSynchronizationOfScenarioTestClasses();
        }
      }
    }

    return synchronizationLock;
  }

}
