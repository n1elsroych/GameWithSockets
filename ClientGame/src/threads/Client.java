package threads;

import clientEvents.ClientEventsListener;
import clientEvents.ConnectionEvent;
import clientEvents.DisconnectionEvent;
import clientEvents.MessageReceivedEvent;
import formEvents.ClientConnectionEvent;
import formEvents.ClientDisconnectionEvent;
import formEvents.DataReceivedEventEvent;
import formEvents.FormEventsListener;
import formEvents.ValidationErrorEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client implements ClientEventsListener{
    private String serverAddress;
    private int port;
    
    private Socket socket;
    private int sessionId;
    
    private ArrayList<FormEventsListener> listeners;
    
    public Client(String serverAddress, int port) throws IOException{
        this.serverAddress = serverAddress;
        this.port = port;
        listeners = new ArrayList<>();
    }
    
    public void connect(){
        ConnectionHandler connectionHandler = new ConnectionHandler(serverAddress, port);
        connectionHandler.addEventsListener(this);
        connectionHandler.start();
    }
    
    public void addFormEventsListener(FormEventsListener listener){
        listeners.add(listener);
    }
    
    public void triggerConnectionEvent(boolean isConnected){
        ClientConnectionEvent evt = new ClientConnectionEvent(this, isConnected);
        for (FormEventsListener listener : listeners){
            listener.onConnected(evt);
        }
    }
    
    public void setCommunication(int sessionId) throws IOException{
        this.sessionId = sessionId;

//        OutputHandler outputHandler = new OutputHandler(socket.getOutputStream(), sessionId);
//        outputHandler.addEventsListener(this);
//        outputHandler.start();
        
        triggerConnectionEvent(true);
    }
    
    public void sendData(String data) throws IOException{
        DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        out.writeUTF(data);
    }

    public int getSessionId() {
        return sessionId;
    }
    
    private void triggerValidationErrorEvent(String validationError){
        ValidationErrorEvent evt = new ValidationErrorEvent(this, validationError);
        for (FormEventsListener listener : listeners){
            listener.onValidationError(evt);
        }
    }

    @Override
    public void onReceivedMessage(MessageReceivedEvent evt) {
        String message = evt.getMessage();
        triggerDataReceivedEvent(message);
    }
    
    private void triggerDataReceivedEvent(String data){
        DataReceivedEventEvent evt = new DataReceivedEventEvent(this, data);
        for (FormEventsListener listener : listeners){
            listener.onDataReceived(evt);
        }
    }

    @Override
    public void onDisconnected(DisconnectionEvent evt) {
        try {
            socket.close();
            System.out.println("Te has desconectado");
            triggerDisconnectionEvent();
            connect();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    private void triggerDisconnectionEvent(){
        ClientDisconnectionEvent evt = new ClientDisconnectionEvent(this);
        for (FormEventsListener listener : listeners){
            listener.onDisconnection(evt);
        }
    }

    @Override
    public void onConnected(ConnectionEvent evt) {
        try {
            socket = evt.getSocket();
            InputHandler inputHandler = new InputHandler(socket.getInputStream());
            inputHandler.addEventsListener(this);
            inputHandler.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
