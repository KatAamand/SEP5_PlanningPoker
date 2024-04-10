package Application;

import Networking.ClientConnection_RMI;
import Networking.Client_RMI;
import java.rmi.RemoteException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ClientFactory implements Runnable {
    private static ClientFactory instance;
    private ClientConnection_RMI client;
    private static final Lock lock = new ReentrantLock();

    private ClientFactory() throws RemoteException {
    }

    public static synchronized ClientFactory getInstance() throws RemoteException {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ClientFactory();
                }
            }
        }
        return instance;
    }

    public ClientConnection_RMI getClient() throws RemoteException {
        if (client == null) {
            client = new Client_RMI();
        }
        return client;
    }

    @Override
    public void run() {

    }
}
