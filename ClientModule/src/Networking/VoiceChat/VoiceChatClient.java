/* package Networking.VoiceChat;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class VoiceChatClient {
    private static final String SERVER_ADDRESS = "192.168.1.166";  // Skift til serverens IP-adresse
    private static int serverPort;
    private DatagramSocket clientSocket;
    private Thread sendThread;
    private Thread receiveThread;
    private boolean running;
    private TargetDataLine microphone;
    private SourceDataLine speakers;
    private static VoiceChatClient instance;
    private static final Lock lock = new ReentrantLock();

    private VoiceChatClient(int serverPort) {
        try {
            microphone = AudioUtils.getTargetDataLine();
            speakers = AudioUtils.getSourceDataLine();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        try {
            clientSocket = new DatagramSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        running = false;
        this.serverPort = serverPort;
    }

    public static VoiceChatClient getInstance(int serverPortToSet)
    {
        //Here we use the "Double-checked lock" principle to ensure proper synchronization.
        if(instance == null)
        {
            synchronized (lock)
            {
                if(instance == null)
                {
                    instance = new VoiceChatClient(serverPortToSet);
                }
            }
        }
        serverPort = serverPortToSet;
        return instance;
    }

    public void start() {
        if (running)
        {
            try {
                microphone.open();
                microphone.start();
                speakers.open();
                speakers.start();
            } catch (LineUnavailableException e) {
                throw new RuntimeException(e);
            }

        }
        else {
            running = true;

            sendThread = new Thread(() -> {
                try {
                    InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                    byte[] buffer = new byte[1024];
                    microphone.start();

                    while (running) {
                        int bytesRead = microphone.read(buffer, 0, buffer.length);
                        DatagramPacket sendPacket = new DatagramPacket(buffer, bytesRead, serverAddress, serverPort);
                        clientSocket.send(sendPacket);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            receiveThread = new Thread(() -> {
                try {
                    byte[] receiveBuffer = new byte[1024];
                    speakers.start();

                    while (running) {
                        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                        clientSocket.receive(receivePacket);
                        speakers.write(receivePacket.getData(), 0, receivePacket.getLength());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            sendThread.start();
            receiveThread.start();
        }
    }

    public void stop() {
        microphone.stop();
        microphone.close();
        speakers.stop();
        speakers.close();
    }
}

 */

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
    private static final String SERVER_ADDRESS = "192.168.1.166";  // Skift til serverens IP-adresse
    private static int serverPort;
    private DatagramSocket clientSocket;
    private Thread sendThread;
    private Thread receiveThread;
    private volatile boolean running;
    private TargetDataLine microphone;
    private SourceDataLine speakers;
    private static VoiceChatClient instance;
    private static final Lock lock = new ReentrantLock();

    private final Object sendLock = new Object();
    private final Object receiveLock = new Object();

    private VoiceChatClient(int serverPort) {
        try {
            microphone = AudioUtils.getTargetDataLine();
            speakers = AudioUtils.getSourceDataLine();
            clientSocket = new DatagramSocket();
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        this.serverPort = serverPort;
    }

    public static VoiceChatClient getInstance(int serverPortToSet) {
        lock.lock();
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

    public void start() {
        if (running) return;

        running = true;
        try {
            microphone.open();
            microphone.start();
            speakers.open();
            speakers.start();
        } catch (LineUnavailableException e) {
            throw new RuntimeException(e);
        }

        sendThread = new Thread(() -> {
            try {
                InetAddress serverAddress = InetAddress.getByName(SERVER_ADDRESS);
                byte[] buffer = new byte[1024];

                while (running) {
                    int bytesRead = microphone.read(buffer, 0, buffer.length);
                    DatagramPacket sendPacket = new DatagramPacket(buffer, bytesRead, serverAddress, serverPort);
                    synchronized (sendLock) {
                        clientSocket.send(sendPacket);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        receiveThread = new Thread(() -> {
            try {
                byte[] receiveBuffer = new byte[1024];

                while (running) {
                    DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                    synchronized (receiveLock) {
                        clientSocket.receive(receivePacket);
                    }
                    synchronized (speakers) {
                        speakers.write(receivePacket.getData(), 0, receivePacket.getLength());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        sendThread.start();
        receiveThread.start();
    }

    public void stop() {
        running = false;
        try {
            if (sendThread != null) {
                sendThread.join();
            }
            if (receiveThread != null) {
                receiveThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (microphone != null) {
                microphone.stop();
                microphone.close();
            }
            if (speakers != null) {
                speakers.stop();
                speakers.close();
            }
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        }
    }
}
