package threads;

import clientEvents.ClientEventsListener;
import clientEvents.ConnectionEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class ConnectionHandler extends Thread{
    private String serverAddress;
    private int port;
        
    private ArrayList<ClientEventsListener> listeners;
    
    public ConnectionHandler(String serverAddress, int port){
        this.serverAddress = serverAddress;
        this.port = port;
        listeners = new ArrayList<>();
    }

    @Override
    public void run() {
        boolean isConnected = false;
        while (!isConnected){
            try {
                Socket socket = new Socket(serverAddress, port);
                isConnected = true;
                triggerConnectionEvent(socket);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public void triggerConnectionEvent(Socket socket){
        ConnectionEvent evt = new ConnectionEvent(this ,socket);
        for (ClientEventsListener listener : listeners){
            listener.onConnected(evt);
        }
    }
    
    public void addEventsListener(ClientEventsListener listener){
        listeners.add(listener);
    }
}
