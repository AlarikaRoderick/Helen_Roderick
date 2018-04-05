package chat.server;

import chat.network.TCPConnection;
import chat.network.TCPConnectionListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ChatServer implements TCPConnectionListener{

    public static void main(String[] args) {
        new ChatServer();
    }

    private final ArrayList<TCPConnection> connections = new ArrayList<>();

    private ChatServer(){
        System.out.println("Server is running...");
        try(ServerSocket serverSocket = new ServerSocket(8189)){
            while(true){
                try{
                    new TCPConnection(this, serverSocket.accept());
                } catch(IOException e){
                    System.out.println("TCPConnection exception: " + e);
                }
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }


    @Override
    public synchronized void OnConnectionReady(TCPConnection tcpconnection) {
        connections.add(tcpconnection);
        sendToAllConnections("Client connected" + tcpconnection);
    }

    @Override
    public synchronized void OnReceiveString(TCPConnection tcpconnection, String value) {
        sendToAllConnections(value);
    }

    @Override
    public synchronized void OnDisconnect(TCPConnection tcpconnection) {
        connections.remove(tcpconnection);
        sendToAllConnections("Client disconnected" + tcpconnection);
    }

    @Override
    public synchronized void OnException(TCPConnection tcpconnection, Exception e) {
        System.out.println("TCPConnection exception: " + e);
    }

    private void sendToAllConnections(String value){
        System.out.println(value);
        for (int i = 0; i < connections.size(); i++)
            connections.get(i).SendString(value);
    }
}
