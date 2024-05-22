package Application;

import Networking.ServerConnection_RMI;
import Networking.Server_RMI;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class RunServer {
    public static void main(String[] args) {
        try {
            ServerConnection_RMI server = new Server_RMI();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("Model", server);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }


        System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": Server is started");
    }
}
