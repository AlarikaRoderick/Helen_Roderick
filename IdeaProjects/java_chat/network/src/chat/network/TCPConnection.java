package chat.network;

import java.io.*;
import java.net.Socket;
import java.nio.charset.Charset;

public class TCPConnection {

    private final Socket socket;
    private final Thread rxThread;
    private final BufferedReader in;
    private final BufferedWriter out;
    private final TCPConnectionListener eventListener;

    public TCPConnection(TCPConnectionListener eventListener, String ipAddres, int port) throws IOException {
        this(eventListener, new Socket(ipAddres, port));

    }

    public TCPConnection(TCPConnectionListener eventListener, Socket socket) throws IOException{

        this.eventListener = eventListener;
        this.socket = socket;
        socket.getInputStream();
        in = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));

        rxThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    eventListener.OnConnectionReady(TCPConnection.this);

                    while(!rxThread.isInterrupted()){
                        eventListener.OnReceiveString(TCPConnection.this, in.readLine());
                    }
                } catch(IOException e){
                    eventListener.OnException(TCPConnection.this, e);
                } finally{
                    eventListener.OnDisconnect(TCPConnection.this);
                }
            }
        });
        rxThread.start();
    }

    public synchronized void SendString(String value){
        try {
            out.write(value + "\r\n");
            out.flush();
        } catch (IOException e) {
            eventListener.OnException(TCPConnection.this, e);
            Disconnect();
        }
    }

    public synchronized void Disconnect(){
        rxThread.interrupt();
        try {
            socket.close();
        } catch (IOException e) {
            eventListener.OnException(TCPConnection.this, e);
        }
    }

    @Override
    public String toString() {
        return "TCPConnection: " + socket.getInetAddress() + ": " + socket.getPort();
    }
}
