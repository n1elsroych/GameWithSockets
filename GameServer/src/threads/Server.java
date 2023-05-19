package threads;

import gameEvents.DataPackageReceivedEvent;
import gameEvents.GameEventsListener;
import gameEvents.UserConnectedEvent;
import gameEvents.UserDisconnectedEvent;
import socketEvents.ClientConnectedEvent;
import socketEvents.ClientDisconnectedEvent;
import socketEvents.MessageReceivedEvent;
import socketEvents.ServerEventsListener;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Server implements ServerEventsListener{
    private ServerSocket serverSocket;
    private Map<Integer, Socket> clients;
    ConnectionsHandler connectionsHandler;
    DisconnectionsHandler disconnectionsHandler;
    ArrayList<GameEventsListener> listeners;
    
    private int sessionID;
    
    public Server(int port) throws IOException{
        serverSocket = new ServerSocket(port);
        clients = new HashMap<>();
        sessionID = 0;
        listeners = new ArrayList<>();
        
        System.out.println("Servidor iniciado en el puerto " + port);
    }
    
    public void start() throws IOException{
        connectionsHandler = new ConnectionsHandler(serverSocket);
        connectionsHandler.addEventsListener(this);
        connectionsHandler.start();
        
        disconnectionsHandler = new DisconnectionsHandler(clients);
        disconnectionsHandler.addEventsListener(this);
        disconnectionsHandler.start();
    }
    
    public void sendUnicast(int sessionId, String message) throws IOException {
        synchronized (clients) {
            Socket clientSocket = clients.get(sessionId);
            OutputTask out = new OutputTask(clientSocket.getOutputStream(), message);
            out.start();
        }
    }
    
    public void sendBroadcast(String message) throws IOException{
        DataOutputStream out;
        synchronized (clients) {
            for (Socket clientSocket: clients.values()) {
                out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeUTF(message);
            }
        }
    }
    
    public void sendMulticast(String message, ArrayList<Integer> sessions) throws IOException{
        synchronized (clients) {
            for (int session : sessions){
                Socket clientSocket = clients.get(session);
                OutputTask out = new OutputTask(clientSocket.getOutputStream(), message);
                out.start();
            }
        }
    }
    
    
    public void shutdown(){
        try {
            serverSocket.close();
            connectionsHandler.interrupt();
            disconnectionsHandler.interrupt();
            synchronized (clients) {
                for (Socket clientSocket : clients.values()){
                    clientSocket.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    @Override
    public void onUserConnected(ClientConnectedEvent evt) {
        try {
            Socket clientSocket = evt.getSocket();
            sessionID++;
            synchronized (clients) {
                clients.put(sessionID, clientSocket);
            }
                System.out.println("Se agrego un usuario a la lista con el ID = "+sessionID);  
                
            triggerUserConnectedEvent(sessionID);

            ClientHandler clientHandler = new ClientHandler(clientSocket.getInputStream(), sessionID);
            clientHandler.addEventsListener(this);
            clientHandler.start();            
        } catch(IOException ex) {
            ex.printStackTrace();
            //clientId--;
        }
    }
    
    private void triggerUserConnectedEvent(int sessionId){
        UserConnectedEvent evt = new UserConnectedEvent(this, sessionId);
        for (GameEventsListener listener : listeners){
            listener.onUserConnected(evt);
        }
    }
    
    @Override
    public void onReceivedMessage(MessageReceivedEvent evt) {
        String message = evt.getMessage();
        triggerDataPackageReceivedEvent(message); 
    }
    
    @Override
    public void onClientDisconnected(ClientDisconnectedEvent evt) {
        int sessionId = evt.getId();
        synchronized(clients){
            try {
                clients.get(sessionId).close();
                clients.remove(sessionId);
                System.out.println("El cliente  con ID = "+sessionId+" ya esta fuera de la lista de sockets");
                triggerUserDisconnectedEvent(sessionId);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    private void triggerUserDisconnectedEvent(int sessionId){
        UserDisconnectedEvent evt =  new UserDisconnectedEvent(this, sessionId);
        for (GameEventsListener listener : listeners){
            listener.onUserDisconnected(evt);
        }
    }
    
    public void sendValidationError(int sessionID, String message) throws IOException{
        Socket clientSocket = clients.get(sessionID);
        OutputTask outputHandler = new OutputTask(clientSocket.getOutputStream(), message);
        outputHandler.start();
    }

    public void sendConfirmation(int sessionID) throws IOException {
        Socket clientSocket = clients.get(sessionID);
        OutputTask outputHandler = new OutputTask(clientSocket.getOutputStream(), "<success>");
        outputHandler.start();
    }
    
    public void addEventsListener(GameEventsListener listener){
        listeners.add(listener);
    }
    
    public void removeEventsListener(GameEventsListener listener) {
        listeners.remove(listener);
    }
    
    private void triggerDataPackageReceivedEvent(String message){
        DataPackageReceivedEvent evt = new DataPackageReceivedEvent(this, message);
        for (GameEventsListener listener : listeners){
            listener.onMessageReceived(evt);
        }
    }
}
