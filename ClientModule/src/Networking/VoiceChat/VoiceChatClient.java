package Networking.VoiceChat;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VoiceChatClient {
    // Constants for server address and port
    private static final String SERVER_ADDRESS = "localhost";  // Change to the server's IP address
    private static int serverPort;

    // Datagram socket for network communication
    private DatagramSocket clientSocket;

    // Threads for sending and receiving voice data
    private Thread sendThread;
    private Thread receiveThread;

    // Flag to indicate if the client is running
    private volatile boolean running;

    // Audio lines for capturing and playing sound
    private TargetDataLine microphone;
    private SourceDataLine speakers;

    // Singleton instance of the VoiceChatClient
    private static VoiceChatClient instance;

    // Locks for synchronization
    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();
    private static final Lock lock = new ReentrantLock();

    // Private constructor for singleton pattern
    private VoiceChatClient(int serverPort) {
        try {
            // Initialize microphone and speakers using utility methods
            microphone = AudioUtils.getTargetDataLine();
            speakers = AudioUtils.getSourceDataLine();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        this.serverPort = serverPort;
    }

    // Method to get the singleton instance of VoiceChatClient
    public static VoiceChatClient getInstance(int serverPortToSet) {
        lock.lock(); // Ensure thread-safe access to the instance
        try {
            if (instance == null) {
                instance = new VoiceChatClient(serverPortToSet);
            }
            serverPort = serverPortToSet;
        } finally {
            lock.unlock();
        }
        return instance;
    }

    // Initialize the DatagramSocket
    private void initializeSocket() {
        try {
            // Close existing socket if open
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            clientSocket = new DatagramSocket(); // Create new DatagramSocket
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize DatagramSocket");
        }
    }

    // Start the voice chat client
    public void start() {
        if (running) return; // Return if already running

        running = true; // Set running flag to true
        try {
            // Open and start microphone and speakers
            microphone.open();
            microphone.start();
            speakers.open();
            speakers.start();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        initializeSocket(); // Initialize the socket

        // Thread for sending audio data to the server
        sendThread = new Thread(() -> {
            try {
                InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS); // Get server address
                byte[] buffer = new byte[1024]; // Buffer for audio data

                while (running) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length); // Read audio data from microphone
                    DatagramPacket sendPacket = new DatagramPacket(buffer, bytesRead, serverAddress, serverPort); // Create packet
                    synchronized (sendLock) {
                        clientSocket.send(sendPacket); // Send packet to server
                    }
                }
            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        });

        // Thread for receiving audio data from the server
        receiveThread = new Thread(() -> {
            try {
                byte[] receiveBuffer = new byte[1024]; // Buffer for receiving data

                while (running) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length); // Create packet for receiving data
                    synchronized (receiveLock) {
                        clientSocket.receive(receivePacket); // Receive packet from server
                    }
                    synchronized (speakers) {
                        speakers.write(receivePacket.getData(), 0, receivePacket.getLength()); // Write data to speakers
                    }
                }
            } catch (Exception e) {
                if (running) {
                    e.printStackTrace();
                }
            }
        });

        // Start send and receive threads
        sendThread.start();
        receiveThread.start();
    }

    // Stop the voice chat client
    public void stop() {
        running = false; // Set running flag to false

        try {
            // Join send and receive threads
            if (sendThread != null) {
                sendThread.join();
            }
            if (receiveThread != null) {
                receiveThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            // Stop and close microphone and speakers
            if (microphone != null) {
                microphone.stop();
                microphone.close();
            }
            if (speakers != null) {
                speakers.stop();
                speakers.close();
            }
            // Close the socket if open
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
    }
}
