package formEvents;

public interface FormEventsListener {

    public void onConnected(ClientConnectionEvent evt);
    
    public void onValidationError(ValidationErrorEvent evt);
    
    public void onDataReceived(DataReceivedEventEvent evt);
    
    public void onDisconnection(ClientDisconnectionEvent evt);
}
