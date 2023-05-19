package clientEvents;

public interface ClientEventsListener {
    
    public void onConnected(ConnectionEvent evt);
    
    public void onReceivedMessage(MessageReceivedEvent evt);
    
    public void onDisconnected(DisconnectionEvent evt);
}
