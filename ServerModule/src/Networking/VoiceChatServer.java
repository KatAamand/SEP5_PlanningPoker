package Networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceChatServer implements Runnable {
    private int SERVER_PORT;
    private DatagramSocket serverSocket;
    private Map<String, Integer> clients = new ConcurrentHashMap<>();

    public VoiceChatServer(int serverPort) {
        this.SERVER_PORT = serverPort;
        try {
            serverSocket = new DatagramSocket(SERVER_PORT);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to start server on port " + SERVER_PORT);
        }
    }

    @Override
    public void run() {
        byte[] receiveBuffer = new byte[1024];

        while (true) {
            try {
                //Read incoming voice chat packets
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);

                //If client doesnt exist i client hashMap, add client to the hashMap
                String clientKey = receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort();
                if (!clients.containsKey(clientKey)) {
                    clients.put(clientKey, receivePacket.getPort());
                    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ", VoiceChatServer: New client connected: " + clientKey);
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
