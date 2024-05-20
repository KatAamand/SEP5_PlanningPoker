package Application;

import Networking.ServerConnection_RMI;
import Networking.Server_RMI;
import Networking.VoiceChatServer;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class RunServer {
    public static void main(String[] args) {
        try {
            ServerConnection_RMI server = new Server_RMI();
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.bind("Model", server);
        } catch (RemoteException | AlreadyBoundException e) {
            throw new RuntimeException(e);
        }
        /*
        VoiceChatServer voiceServer = new VoiceChatServer();
        Thread serverThread = new Thread(voiceServer);
        serverThread.start();
        System.out.println("Voice chat server is started");

         */

        System.out.println("Server is started");
    }
}
