package Networking;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class VoiceChatServer implements Runnable {
    private int SERVER_PORT;
    private DatagramSocket serverSocket;
    private Map<String, Integer> clients = new HashMap<>();

    public VoiceChatServer(int serverPort) {
        try {
            serverSocket = new DatagramSocket(serverPort);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];

        while (true) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                String clientKey = receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort();
                if (!clients.containsKey(clientKey)) {
                    clients.put(clientKey, receivePacket.getPort());
                    System.out.println("New client connected: " + clientKey);
                }

                // Forward packet to all other clients
                for (Map.Entry<String, Integer> client : clients.entrySet()) {
                    if (!client.getKey().equals(clientKey)) {
                        InetAddress clientAddress = InetAddress.getByName(client.getKey().split(":")[0]);
                        int clientPort = client.getValue();
                        DatagramPacket sendPacket = new DatagramPacket(receivePacket.getData(), receivePacket.getLength(), clientAddress, clientPort);
                        serverSocket.send(sendPacket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

