package Application;

import Model.ClientModel;
import Model.ClientModel_Impl;

import java.rmi.RemoteException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ModelFactory {
    private static ModelFactory instance;
    private static final Lock lock = new ReentrantLock();
    private ClientModel model;
    private ClientFactory clientFactory;

    private ModelFactory(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public static synchronized ModelFactory getInstance(ClientFactory clientFactory) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new ModelFactory(clientFactory);
                }
            }
        }
        return instance;
    }

    public ClientModel getClientModel() throws RemoteException {
        if (model == null) {
            model = new ClientModel_Impl(clientFactory.getClient());
        }
        return model;
    }

}
