package Views;

import Networking.ServerConnection_RMI;
import Networking.Server_RMI;

import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/** This is a test server implementation used in the automated scenario test classes*/
public class TestServer
{
  private static final Lock localLock = new ReentrantLock();
  private volatile static ServerConnection_RMI instance;
  private static Registry registry;


  public static ServerConnection_RMI getInstance() throws RemoteException, AlreadyBoundException
  {
    //Here we use the "Double-checked lock" principle to ensure proper synchronization.
    if(instance == null) {
      synchronized (localLock) {
        if(instance == null) {
          instance = new Server_RMI();
          registry = LocateRegistry.createRegistry(1099);
          registry.bind("Model", instance);
        }
      }
    }
    return instance;
  }


  public static void resetServer() throws RemoteException, NotBoundException
  {
    // Currently does nothing.
  }
}
